package com.haidainversiones.haidainversionesllantas;

import com.haidainversiones.haidainversionesllantas.entity.CarritoItem;
import com.haidainversiones.haidainversionesllantas.entity.Producto;
import com.haidainversiones.haidainversionesllantas.entity.Usuario;
import com.haidainversiones.haidainversionesllantas.exception.BadRequestException;
import com.haidainversiones.haidainversionesllantas.exception.ResourceNotFoundException;
import com.haidainversiones.haidainversionesllantas.repository.CarritoItemRepository;
import com.haidainversiones.haidainversionesllantas.repository.ProductoRepository;
import com.haidainversiones.haidainversionesllantas.repository.UsuarioRepository;
import com.haidainversiones.haidainversionesllantas.service.CarritoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CarritoService Tests")
class CarritoServiceTest {

    @Mock private CarritoItemRepository carritoItemRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CarritoService carritoService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Llanta Test");
        producto.setPrecio(new BigDecimal("300.00"));
        producto.setDisponible(true);
        producto.setStock(5);
    }

    @Test
    @DisplayName("Debe agregar nuevo item al carrito de invitado")
    void agregarAlCarrito_NuevoItem_DebeCrearItem() {
        // Given
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(carritoItemRepository.findBySessionIdAndProductoId("sess-1", 1L)).thenReturn(Optional.empty());
        CarritoItem nuevoItem = new CarritoItem();
        nuevoItem.setCantidad(2);
        when(carritoItemRepository.save(any())).thenReturn(nuevoItem);

        // When
        CarritoItem result = carritoService.agregarAlCarrito("sess-1", 1L, 2);

        // Then
        assertNotNull(result);
        verify(carritoItemRepository).save(any(CarritoItem.class));
    }

    @Test
    @DisplayName("Debe incrementar cantidad si el item ya existe en el carrito")
    void agregarAlCarrito_ItemExistente_DebeIncrementarCantidad() {
        // Given
        CarritoItem itemExistente = new CarritoItem();
        itemExistente.setCantidad(1);
        itemExistente.setProducto(producto);
        itemExistente.setPrecioUnitario(new BigDecimal("300.00"));

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(carritoItemRepository.findBySessionIdAndProductoId("sess-1", 1L))
                .thenReturn(Optional.of(itemExistente));
        when(carritoItemRepository.save(any())).thenReturn(itemExistente);

        // When
        carritoService.agregarAlCarrito("sess-1", 1L, 3);

        // Then
        assertEquals(4, itemExistente.getCantidad()); // 1 + 3
    }

    @Test
    @DisplayName("Debe lanzar excepción si el producto no está disponible")
    void agregarAlCarrito_ProductoNoDisponible_DebeLanzarBadRequest() {
        // Given
        producto.setDisponible(false);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // When & Then
        assertThrows(BadRequestException.class,
                () -> carritoService.agregarAlCarrito("sess-1", 1L, 1));
    }

    @Test
    @DisplayName("Debe lanzar excepción si sessionId es nulo")
    void agregarAlCarrito_SessionIdNulo_DebeLanzarBadRequest() {
        assertThrows(BadRequestException.class,
                () -> carritoService.agregarAlCarrito(null, 1L, 1));
    }

    @Test
    @DisplayName("Debe calcular total correctamente")
    void calcularTotal_VariosItems_DebeRetornarSuma() {
        // Given
        CarritoItem item1 = new CarritoItem();
        item1.setCantidad(2);
        item1.setPrecioUnitario(new BigDecimal("100.00")); // subtotal 200

        CarritoItem item2 = new CarritoItem();
        item2.setCantidad(1);
        item2.setPrecioUnitario(new BigDecimal("300.00")); // subtotal 300

        // When
        BigDecimal total = carritoService.calcularTotal(List.of(item1, item2));

        // Then
        assertEquals(new BigDecimal("500.00"), total);
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar item inexistente")
    void actualizarCantidad_ItemNoExiste_DebeLanzarResourceNotFound() {
        // Given
        when(carritoItemRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> carritoService.actualizarCantidad(99L, 3));
    }

    @Test
    @DisplayName("Debe agregar al carrito de usuario registrado correctamente")
    void agregarAlCarritoUsuario_Exitoso_DebeGuardarConUsuario() {
        // Given
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(carritoItemRepository.findByUsuarioIdAndProductoId(1L, 1L)).thenReturn(Optional.empty());

        CarritoItem nuevoItem = new CarritoItem();
        nuevoItem.setUsuario(usuario);
        nuevoItem.setCantidad(1);
        when(carritoItemRepository.save(any())).thenReturn(nuevoItem);

        // When
        CarritoItem result = carritoService.agregarAlCarritoUsuario(1L, 1L, 1);

        // Then
        assertNotNull(result);
        assertNotNull(result.getUsuario());
    }
}
