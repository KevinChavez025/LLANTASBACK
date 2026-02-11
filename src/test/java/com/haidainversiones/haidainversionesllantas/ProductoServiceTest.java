package com.haidainversiones.haidainversionesllantas;

import com.haidainversiones.haidainversionesllantas.entity.Producto;
import com.haidainversiones.haidainversionesllantas.exception.ResourceNotFoundException;
import com.haidainversiones.haidainversionesllantas.repository.ProductoRepository;
import com.haidainversiones.haidainversionesllantas.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        // Preparar un producto de prueba
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Llanta Michelin");
        producto.setMarca("Michelin");
        producto.setPrecio(new BigDecimal("450.00"));
        producto.setStock(10);
        producto.setDisponible(true);
    }

    @Test
    void guardar_DeberiaGuardarProducto() {
        // Given (Dado)
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // When (Cuando)
        Producto resultado = productoService.guardar(producto);

        // Then (Entonces)
        assertNotNull(resultado);
        assertEquals("Llanta Michelin", resultado.getNombre());
        assertEquals("Michelin", resultado.getMarca());
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void obtenerTodos_DeberiaRetornarListaDeProductos() {
        // Given
        Producto producto2 = new Producto();
        producto2.setId(2L);
        producto2.setNombre("Llanta Bridgestone");
        producto2.setMarca("Bridgestone");

        List<Producto> productos = Arrays.asList(producto, producto2);
        when(productoRepository.findAll()).thenReturn(productos);

        // When
        List<Producto> resultado = productoService.obtenerTodos();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void getProductoById_DeberiaRetornarProducto_CuandoExiste() {
        // Given
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // When
        Optional<Producto> resultado = productoService.getProductoById(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("Llanta Michelin", resultado.get().getNombre());
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    void getProductoById_DeberiaRetornarVacio_CuandoNoExiste() {
        // Given
        when(productoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Optional<Producto> resultado = productoService.getProductoById(999L);

        // Then
        assertFalse(resultado.isPresent());
        verify(productoRepository, times(1)).findById(999L);
    }

    @Test
    void obtenerPorIdOrThrow_DeberiaLanzarExcepcion_CuandoNoExiste() {
        // Given
        when(productoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            productoService.obtenerPorIdOrThrow(999L);
        });

        verify(productoRepository, times(1)).findById(999L);
    }

    @Test
    void deleteProducto_DeberiaEliminarProducto() {
        // Given
        doNothing().when(productoRepository).deleteById(1L);

        // When
        productoService.deleteProducto(1L);

        // Then
        verify(productoRepository, times(1)).deleteById(1L);
    }

    @Test
    void obtenerPorMarca_DeberiaRetornarProductosDeLaMarca() {
        // Given
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findByMarcaContainingIgnoreCase("Michelin")).thenReturn(productos);

        // When
        List<Producto> resultado = productoService.obtenerPorMarca("Michelin");

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Michelin", resultado.get(0).getMarca());
        verify(productoRepository, times(1)).findByMarcaContainingIgnoreCase("Michelin");
    }

    @Test
    void obtenerDisponibles_DeberiaRetornarSoloDisponibles() {
        // Given
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findByDisponibleTrue()).thenReturn(productos);

        // When
        List<Producto> resultado = productoService.obtenerDisponibles();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getDisponible());
        verify(productoRepository, times(1)).findByDisponibleTrue();
    }
}
