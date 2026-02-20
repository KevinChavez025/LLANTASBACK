package com.haidainversiones.haidainversionesllantas;

import com.haidainversiones.haidainversionesllantas.dto.CrearPedidoRequest;
import com.haidainversiones.haidainversionesllantas.dto.PedidoResponse;
import com.haidainversiones.haidainversionesllantas.entity.*;
import com.haidainversiones.haidainversionesllantas.enums.EstadoPedido;
import com.haidainversiones.haidainversionesllantas.exception.BadRequestException;
import com.haidainversiones.haidainversionesllantas.exception.ResourceNotFoundException;
import com.haidainversiones.haidainversionesllantas.repository.*;
import com.haidainversiones.haidainversionesllantas.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoService Tests")
class PedidoServiceTest {

    @Mock private PedidoRepository pedidoRepository;
    @Mock private DetallePedidoRepository detallePedidoRepository;
    @Mock private CarritoItemRepository carritoItemRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private IdempotencyKeyRepository idempotencyKeyRepository;
    @Mock private PedidoHistorialRepository pedidoHistorialRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Producto producto;
    private CarritoItem carritoItem;
    private CrearPedidoRequest request;
    private Pedido pedidoGuardado;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Llanta Michelin 205/55R16");
        producto.setMarca("Michelin");
        producto.setMedida("205/55R16");
        producto.setPrecio(new BigDecimal("450.00"));
        producto.setStock(10);
        producto.setDisponible(true);

        carritoItem = new CarritoItem();
        carritoItem.setId(1L);
        carritoItem.setProducto(producto);
        carritoItem.setCantidad(2);
        carritoItem.setPrecioUnitario(new BigDecimal("450.00"));
        carritoItem.setSessionId("session-123");

        request = new CrearPedidoRequest();
        request.setSessionId("session-123");
        request.setDireccionEnvio("Av. Test 123");
        request.setMetodoPago("EFECTIVO");

        pedidoGuardado = new Pedido();
        pedidoGuardado.setId(1L);
        pedidoGuardado.setNumeroPedido("PED-ABCD1234");
        pedidoGuardado.setEstado(EstadoPedido.PENDIENTE);
        pedidoGuardado.setDetalles(new ArrayList<>());
        pedidoGuardado.setSubtotal(new BigDecimal("900.00"));
        pedidoGuardado.setIgv(new BigDecimal("162.00"));
        pedidoGuardado.setCostoEnvio(new BigDecimal("15.00"));
        pedidoGuardado.setTotal(new BigDecimal("1077.00"));
        pedidoGuardado.setDireccionEnvio("Av. Test 123");
    }

    // ═══════════════════════════════════════════════
    // CREAR PEDIDO — CASOS FELICES
    // ═══════════════════════════════════════════════

    @Test
    @DisplayName("Debe crear pedido exitosamente para usuario invitado")
    void crearPedido_InvitadoConStock_DebeCrearPedido() {
        // Given
        when(idempotencyKeyRepository.findByKeyValue(any())).thenReturn(Optional.empty());
        when(carritoItemRepository.findBySessionId("session-123")).thenReturn(List.of(carritoItem));
        when(productoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(producto));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoGuardado);
        when(detallePedidoRepository.save(any())).thenReturn(new DetallePedido());
        when(productoRepository.save(any())).thenReturn(producto);

        // When
        PedidoResponse response = pedidoService.crearPedido(request);

        // Then
        assertNotNull(response);
        assertEquals("PED-ABCD1234", response.getNumeroPedido());
        verify(carritoItemRepository).deleteBySessionId("session-123");
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Debe retornar pedido existente si idempotency key ya fue usada")
    void crearPedido_IdempotencyKeyDuplicada_DebeRetornarPedidoOriginal() {
        // Given
        request.setIdempotencyKey("uuid-unico-123");

        IdempotencyKey ik = new IdempotencyKey();
        ik.setKeyValue("uuid-unico-123");
        ik.setPedido(pedidoGuardado);

        when(idempotencyKeyRepository.findByKeyValue("uuid-unico-123")).thenReturn(Optional.of(ik));

        // When
        PedidoResponse response = pedidoService.crearPedido(request);

        // Then
        assertNotNull(response);
        assertEquals("PED-ABCD1234", response.getNumeroPedido());
        // No debe tocar el carrito ni crear detalles
        verify(carritoItemRepository, never()).findBySessionId(any());
        verify(pedidoRepository, never()).save(any());
    }

    // ═══════════════════════════════════════════════
    // CREAR PEDIDO — CASOS DE ERROR
    // ═══════════════════════════════════════════════

    @Test
    @DisplayName("Debe lanzar excepción si el carrito está vacío")
    void crearPedido_CarritoVacio_DebeLanzarBadRequest() {
        // Given
        when(idempotencyKeyRepository.findByKeyValue(any())).thenReturn(Optional.empty());
        when(carritoItemRepository.findBySessionId("session-123")).thenReturn(List.of());

        // When & Then
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> pedidoService.crearPedido(request));
        assertEquals("El carrito está vacío", ex.getMessage());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si no hay stock suficiente")
    void crearPedido_StockInsuficiente_DebeLanzarBadRequest() {
        // Given
        producto.setStock(1);           // Solo 1 en stock
        carritoItem.setCantidad(5);     // Piden 5

        when(idempotencyKeyRepository.findByKeyValue(any())).thenReturn(Optional.empty());
        when(carritoItemRepository.findBySessionId("session-123")).thenReturn(List.of(carritoItem));
        when(productoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(producto));

        // When & Then
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> pedidoService.crearPedido(request));
        assertTrue(ex.getMessage().contains("Stock insuficiente"));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el producto no está disponible")
    void crearPedido_ProductoNoDisponible_DebeLanzarBadRequest() {
        // Given
        producto.setDisponible(false);

        when(idempotencyKeyRepository.findByKeyValue(any())).thenReturn(Optional.empty());
        when(carritoItemRepository.findBySessionId("session-123")).thenReturn(List.of(carritoItem));
        when(productoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(producto));

        // When & Then
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> pedidoService.crearPedido(request));
        assertTrue(ex.getMessage().contains("no está disponible"));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el método de pago es inválido")
    void crearPedido_MetodoPagoInvalido_DebeLanzarBadRequest() {
        // Given
        request.setMetodoPago("BITCOIN");

        when(idempotencyKeyRepository.findByKeyValue(any())).thenReturn(Optional.empty());
        when(carritoItemRepository.findBySessionId("session-123")).thenReturn(List.of(carritoItem));
        when(productoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(producto));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoGuardado);

        // When & Then
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> pedidoService.crearPedido(request));
        assertTrue(ex.getMessage().contains("Método de pago inválido"));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el producto no existe en BD")
    void crearPedido_ProductoNoExiste_DebeLanzarResourceNotFound() {
        // Given
        when(idempotencyKeyRepository.findByKeyValue(any())).thenReturn(Optional.empty());
        when(carritoItemRepository.findBySessionId("session-123")).thenReturn(List.of(carritoItem));
        when(productoRepository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> pedidoService.crearPedido(request));
    }

    // ═══════════════════════════════════════════════
    // ACTUALIZAR ESTADO
    // ═══════════════════════════════════════════════

    @Test
    @DisplayName("Debe actualizar el estado del pedido y registrar historial")
    void actualizarEstado_EstadoValido_DebeActualizarYRegistrarHistorial() {
        // Given
        pedidoGuardado.setEstado(EstadoPedido.PENDIENTE);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoGuardado));
        when(pedidoRepository.save(any())).thenReturn(pedidoGuardado);
        when(pedidoHistorialRepository.save(any())).thenReturn(new PedidoHistorial());

        // When
        PedidoResponse response = pedidoService.actualizarEstado(1L, "CONFIRMADO");

        // Then
        assertNotNull(response);
        verify(pedidoHistorialRepository, times(1)).save(any(PedidoHistorial.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el estado es inválido")
    void actualizarEstado_EstadoInvalido_DebeLanzarBadRequest() {
        // Given
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoGuardado));

        // When & Then
        assertThrows(BadRequestException.class,
                () -> pedidoService.actualizarEstado(1L, "ESTADO_FALSO"));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el pedido no existe")
    void actualizarEstado_PedidoNoExiste_DebeLanzarResourceNotFound() {
        // Given
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> pedidoService.actualizarEstado(99L, "CONFIRMADO"));
    }

    // ═══════════════════════════════════════════════
    // OBTENER PEDIDO
    // ═══════════════════════════════════════════════

    @Test
    @DisplayName("Debe retornar pedido por ID cuando existe")
    void obtenerPorId_ExistE_DebeRetornarResponse() {
        // Given
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoGuardado));

        // When
        PedidoResponse response = pedidoService.obtenerPorId(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el pedido no existe")
    void obtenerPorId_NoExiste_DebeLanzarResourceNotFound() {
        // Given
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> pedidoService.obtenerPorId(99L));
    }
}
