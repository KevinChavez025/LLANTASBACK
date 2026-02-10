package com.haidainversiones.haidainversionesllantas.service;

import com.haidainversiones.haidainversionesllantas.dto.CrearPedidoRequest;
import com.haidainversiones.haidainversionesllantas.entity.*;
import com.haidainversiones.haidainversionesllantas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Autowired
    private CarritoItemRepository carritoItemRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Pedido crearPedido(CrearPedidoRequest request) {
        // Obtener items del carrito
        List<CarritoItem> carritoItems;
        if (request.getUsuarioId() != null) {
            carritoItems = carritoItemRepository.findByUsuarioId(request.getUsuarioId());
        } else {
            carritoItems = carritoItemRepository.findBySessionId(request.getSessionId());
        }

        if (carritoItems.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // Crear el pedido
        Pedido pedido = new Pedido();

// Generar número de pedido único
        String numeroPedido = "PED-" + System.currentTimeMillis();
        pedido.setNumeroPedido(numeroPedido);

        pedido.setEstado("PENDIENTE");
        pedido.setFechaPedido(LocalDateTime.now());


        // Asignar usuario si existe
        if (request.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            pedido.setUsuario(usuario);
        } else {
            // Para invitados
            pedido.setNombreInvitado(request.getNombreCliente());
            pedido.setEmailInvitado(request.getEmailCliente());
            pedido.setTelefonoInvitado(request.getTelefonoCliente());
        }

        // Datos de envío
        pedido.setDireccionEnvio(request.getDireccionEnvio());
        pedido.setCiudad(request.getCiudad());
        pedido.setDepartamento(request.getDepartamento());
        pedido.setCodigoPostal(request.getCodigoPostal());
        pedido.setNotasAdicionales(request.getNotasAdicionales());

        // Calcular totales
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CarritoItem item : carritoItems) {
            subtotal = subtotal.add(item.getSubtotal());
        }

        BigDecimal igv = subtotal.multiply(BigDecimal.valueOf(0.18)); // 18% IGV
        BigDecimal costoEnvio = BigDecimal.valueOf(15.00); // Costo fijo de envío
        BigDecimal total = subtotal.add(igv).add(costoEnvio);

        pedido.setSubtotal(subtotal);
        pedido.setIgv(igv);
        pedido.setCostoEnvio(costoEnvio);
        pedido.setTotal(total);

        // Guardar el pedido
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Crear detalles del pedido
        for (CarritoItem item : carritoItems) {
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedidoGuardado);
            detalle.setProducto(item.getProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalle.setSubtotal(item.getSubtotal());
            detallePedidoRepository.save(detalle);
        }

        // Vaciar el carrito
        if (request.getUsuarioId() != null) {
            carritoItemRepository.deleteByUsuarioId(request.getUsuarioId());
        } else {
            carritoItemRepository.deleteBySessionId(request.getSessionId());
        }

        return pedidoGuardado;
    }

    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> obtenerPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> obtenerPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId);
    }

    public Pedido actualizarEstado(Long pedidoId, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }
}
