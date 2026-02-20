package com.haidainversiones.haidainversionesllantas.service;

import com.haidainversiones.haidainversionesllantas.dto.CrearPedidoRequest;
import com.haidainversiones.haidainversionesllantas.dto.PedidoResponse;
import com.haidainversiones.haidainversionesllantas.entity.*;
import com.haidainversiones.haidainversionesllantas.enums.EstadoPago;
import com.haidainversiones.haidainversionesllantas.enums.EstadoPedido;
import com.haidainversiones.haidainversionesllantas.enums.MetodoPago;
import com.haidainversiones.haidainversionesllantas.exception.BadRequestException;
import com.haidainversiones.haidainversionesllantas.exception.ResourceNotFoundException;
import com.haidainversiones.haidainversionesllantas.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final PedidoHistorialRepository pedidoHistorialRepository;

    private static final BigDecimal IGV_RATE = new BigDecimal("0.18");

    @Value("${negocio.costo-envio:15.00}")
    private BigDecimal costoEnvio;

    /**
     * Crea un pedido de forma segura contra:
     *  1. Doble submit (idempotency key)
     *  2. Overselling (lock pesimista por producto)
     *  3. Concurrencia (toda la operación en una sola transacción)
     */
    @Transactional
    public PedidoResponse crearPedido(CrearPedidoRequest request) {

        // ── 1. PROTECCIÓN DOBLE SUBMIT ──────────────────────────────────────
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
            Optional<IdempotencyKey> keyExistente = idempotencyKeyRepository
                    .findByKeyValue(request.getIdempotencyKey());
            if (keyExistente.isPresent()) {
                return mapearAPedidoResponse(keyExistente.get().getPedido());
            }
        }

        // ── 2. OBTENER CARRITO ───────────────────────────────────────────────
        List<CarritoItem> carritoItems;
        if (request.getUsuarioId() != null) {
            carritoItems = carritoItemRepository.findByUsuarioId(request.getUsuarioId());
        } else {
            carritoItems = carritoItemRepository.findBySessionId(request.getSessionId());
        }

        if (carritoItems.isEmpty()) {
            throw new BadRequestException("El carrito está vacío");
        }

        // ── 3. LOCK PESIMISTA + VALIDACIÓN DE STOCK ──────────────────────────
        for (CarritoItem item : carritoItems) {
            Producto producto = productoRepository
                    .findByIdForUpdate(item.getProducto().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto", "id", item.getProducto().getId()));

            if (Boolean.FALSE.equals(producto.getDisponible())) {
                throw new BadRequestException(
                        "El producto '" + producto.getNombre() + "' ya no está disponible.");
            }
            if (producto.getStock() < item.getCantidad()) {
                throw new BadRequestException(
                        "Stock insuficiente para '" + producto.getNombre() +
                        "'. Disponible: " + producto.getStock() +
                        ", solicitado: " + item.getCantidad());
            }
            item.setProducto(producto);
        }

        // ── 4. CREAR PEDIDO ──────────────────────────────────────────────────
        Pedido pedido = new Pedido();
        pedido.setNumeroPedido("PED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setEstadoPago(EstadoPago.PENDIENTE);

        if (request.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Usuario", "id", request.getUsuarioId()));
            pedido.setUsuario(usuario);
        } else {
            pedido.setNombreInvitado(request.getNombreCliente());
            pedido.setEmailInvitado(request.getEmailCliente());
            pedido.setTelefonoInvitado(request.getTelefonoCliente());
        }

        if (request.getMetodoPago() != null) {
            try {
                pedido.setMetodoPago(MetodoPago.valueOf(request.getMetodoPago().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException(
                        "Método de pago inválido: '" + request.getMetodoPago() +
                        "'. Valores válidos: EFECTIVO, TARJETA_CREDITO, TARJETA_DEBITO, TRANSFERENCIA, YAPE, PLIN");
            }
        }

        pedido.setDireccionEnvio(request.getDireccionEnvio());
        pedido.setCiudadEnvio(request.getCiudad());
        pedido.setDistritoEnvio(request.getDistrito());
        pedido.setDepartamentoEnvio(request.getDepartamento());
        pedido.setCodigoPostalEnvio(request.getCodigoPostal());
        pedido.setTelefonoContacto(request.getTelefonoContacto());
        pedido.setNotas(request.getNotasAdicionales());

        // ── 5. CALCULAR TOTALES ──────────────────────────────────────────────
        BigDecimal subtotal = carritoItems.stream()
                .map(CarritoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal igv = subtotal.multiply(IGV_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(igv).add(costoEnvio);

        pedido.setSubtotal(subtotal);
        pedido.setIgv(igv);
        pedido.setCostoEnvio(costoEnvio);
        pedido.setTotal(total);
        pedido.setFechaEntregaEstimada(LocalDateTime.now().plusDays(3));

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // ── 6. CREAR DETALLES Y DESCONTAR STOCK ─────────────────────────────
        for (CarritoItem item : carritoItems) {
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedidoGuardado);
            detalle.setProducto(item.getProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalle.setSubtotal(item.getSubtotal());
            detalle.setNombreProducto(item.getProducto().getNombre());
            detalle.setMarcaProducto(item.getProducto().getMarca());
            detalle.setMedidaProducto(item.getProducto().getMedida());
            detallePedidoRepository.save(detalle);

            Producto producto = item.getProducto();
            producto.descontarStock(item.getCantidad());
            productoRepository.save(producto);
        }

        // ── 7. GUARDAR IDEMPOTENCY KEY ───────────────────────────────────────
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
            try {
                IdempotencyKey ik = new IdempotencyKey();
                ik.setKeyValue(request.getIdempotencyKey());
                ik.setPedido(pedidoGuardado);
                idempotencyKeyRepository.save(ik);
            } catch (DataIntegrityViolationException e) {
                IdempotencyKey existente = idempotencyKeyRepository
                        .findByKeyValue(request.getIdempotencyKey())
                        .orElseThrow();
                return mapearAPedidoResponse(existente.getPedido());
            }
        }

        // ── 8. VACIAR CARRITO ────────────────────────────────────────────────
        if (request.getUsuarioId() != null) {
            carritoItemRepository.deleteByUsuarioId(request.getUsuarioId());
        } else {
            carritoItemRepository.deleteBySessionId(request.getSessionId());
        }

        return mapearAPedidoResponse(pedidoGuardado);
    }

    public Page<PedidoResponse> obtenerTodosPaginado(Pageable pageable) {
        return pedidoRepository.findAll(pageable).map(this::mapearAPedidoResponse);
    }

    public PedidoResponse obtenerPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));
        return mapearAPedidoResponse(pedido);
    }
    @Transactional(readOnly = true)
    public List<PedidoResponse> obtenerPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId).stream()
                .map(this::mapearAPedidoResponse)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza el estado del pedido y registra el cambio en el historial.
     */
    @Transactional
    public PedidoResponse actualizarEstado(Long pedidoId, String nuevoEstadoStr) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", pedidoId));

        EstadoPedido nuevoEstado;
        try {
            nuevoEstado = EstadoPedido.valueOf(nuevoEstadoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    "Estado inválido: '" + nuevoEstadoStr +
                    "'. Valores válidos: PENDIENTE, CONFIRMADO, EN_PREPARACION, ENVIADO, ENTREGADO, CANCELADO");
        }

        EstadoPedido estadoAnterior = pedido.getEstado();

        if (nuevoEstado == EstadoPedido.CANCELADO && estadoAnterior != EstadoPedido.CANCELADO) {
            devolverStockPedido(pedido);
        }

        pedido.setEstado(nuevoEstado);
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // ── AUDITORÍA: registrar el cambio de estado ─────────────────────────
        PedidoHistorial historial = new PedidoHistorial();
        historial.setPedido(pedidoGuardado);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(nuevoEstado);
        historial.setCambiadoPor(obtenerUsuarioActual());
        pedidoHistorialRepository.save(historial);

        return mapearAPedidoResponse(pedidoGuardado);
    }

    public List<PedidoHistorial> obtenerHistorial(Long pedidoId) {
        return pedidoHistorialRepository.findByPedidoIdOrderByFechaCambioAsc(pedidoId);
    }

    /**
     * Devuelve el stock de todos los ítems del pedido.
     * DEBE llamarse siempre dentro de una transacción activa.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    protected void devolverStockPedido(Pedido pedido) {
        for (DetallePedido detalle : pedido.getDetalles()) {
            if (detalle.getProducto() != null) {
                Producto producto = detalle.getProducto();
                producto.setStock(producto.getStock() + detalle.getCantidad());
                if (Boolean.FALSE.equals(producto.getDisponible()) && producto.getStock() > 0) {
                    producto.setDisponible(true);
                }
                productoRepository.save(producto);
            }
        }
    }

    private String obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : "sistema";
    }

    private PedidoResponse mapearAPedidoResponse(Pedido pedido) {
        List<PedidoResponse.DetallePedidoDTO> detallesDTO = pedido.getDetalles().stream()
                .map(d -> PedidoResponse.DetallePedidoDTO.builder()
                        .id(d.getId())
                        .productoId(d.getProducto() != null ? d.getProducto().getId() : null)
                        .nombreProducto(d.getNombreProducto())
                        .marcaProducto(d.getMarcaProducto())
                        .medidaProducto(d.getMedidaProducto())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotal(d.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return PedidoResponse.builder()
                .id(pedido.getId())
                .numeroPedido(pedido.getNumeroPedido())
                .estado(pedido.getEstado() != null ? pedido.getEstado().name() : null)
                .metodoPago(pedido.getMetodoPago() != null ? pedido.getMetodoPago().name() : null)
                .estadoPago(pedido.getEstadoPago() != null ? pedido.getEstadoPago().name() : null)
                .subtotal(pedido.getSubtotal())
                .igv(pedido.getIgv())
                .costoEnvio(pedido.getCostoEnvio())
                .total(pedido.getTotal())
                .direccionEnvio(pedido.getDireccionEnvio())
                .ciudadEnvio(pedido.getCiudadEnvio())
                .distritoEnvio(pedido.getDistritoEnvio())
                .departamentoEnvio(pedido.getDepartamentoEnvio())
                .codigoPostalEnvio(pedido.getCodigoPostalEnvio())
                .telefonoContacto(pedido.getTelefonoContacto())
                .usuarioId(pedido.getUsuario() != null ? pedido.getUsuario().getId() : null)
                .emailCliente(pedido.getUsuario() != null
                        ? pedido.getUsuario().getEmail() : pedido.getEmailInvitado())
                .nombreCliente(pedido.getUsuario() != null
                        ? pedido.getUsuario().getNombreCompleto() : pedido.getNombreInvitado())
                .detalles(detallesDTO)
                .fechaCreacion(pedido.getFechaCreacion())
                .fechaEntregaEstimada(pedido.getFechaEntregaEstimada())
                .build();
    }
    /**
     * Verifica que el usuarioId pertenece al email autenticado.
     * Lanza UnauthorizedException si intenta acceder a pedidos de otro usuario.
     */
    public void verificarOwnership(Long usuarioId, String emailAutenticado) {
        usuarioRepository.findById(usuarioId).ifPresent(usuario -> {
            if (!usuario.getEmail().equals(emailAutenticado)) {
                throw new com.haidainversiones.haidainversionesllantas.exception.UnauthorizedException(
                    "No tienes permisos para ver los pedidos de otro usuario.");
            }
        });
    }

}
