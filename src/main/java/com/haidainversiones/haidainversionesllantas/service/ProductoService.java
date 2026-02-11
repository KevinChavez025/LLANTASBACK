package com.haidainversiones.haidainversionesllantas.service;

import com.haidainversiones.haidainversionesllantas.entity.Producto;
import com.haidainversiones.haidainversionesllantas.exception.ResourceNotFoundException;
import com.haidainversiones.haidainversionesllantas.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // ========== MÉTODOS BÁSICOS CRUD ==========

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    // ✅ AGREGAR ESTE MÉTODO
    public Producto obtenerPorIdOrThrow(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
    }

    public Producto guardar(Producto producto) {
        if (producto.getFechaCreacion() == null) {
            producto.setFechaCreacion(LocalDateTime.now());
        }
        producto.setFechaActualizacion(LocalDateTime.now());
        return productoRepository.save(producto);
    }

    public Producto actualizar(Long id, Producto producto) {
        // Verificar que existe
        obtenerPorIdOrThrow(id);

        producto.setId(id);
        producto.setFechaActualizacion(LocalDateTime.now());
        return productoRepository.save(producto);
    }

    public void eliminar(Long id) {
        // Verificar que existe
        obtenerPorIdOrThrow(id);
        productoRepository.deleteById(id);
    }

    // ========== MÉTODOS DE FILTRADO ==========

    public List<Producto> obtenerDestacados() {
        return productoRepository.findByEsDestacadoTrue();
    }

    public List<Producto> obtenerNuevos() {
        return productoRepository.findByEsNuevoTrue();
    }

    public List<Producto> obtenerDisponibles() {
        return productoRepository.findByDisponibleTrue();
    }

    public List<Producto> obtenerPorMarca(String marca) {
        return productoRepository.findByMarca(marca);
    }

    public List<Producto> obtenerPorModelo(String modelo) {
        return productoRepository.findByModelo(modelo);
    }

    public List<Producto> obtenerPorTipoVehiculo(String tipoVehiculo) {
        return productoRepository.findByTipoVehiculo(tipoVehiculo);
    }

    public List<Producto> obtenerPorMedida(String medida) {
        return productoRepository.findByMedida(medida);
    }

    // ========== BÚSQUEDA Y FILTROS AVANZADOS ==========

    public List<Producto> buscarPorTexto(String query) {
        return productoRepository.buscarPorTexto(query);
    }

    public List<Producto> obtenerPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    public List<Producto> obtenerRecientes() {
        return productoRepository.findTop10ByOrderByFechaCreacionDesc();
    }

    public List<Producto> obtenerPorRangoPrecio(BigDecimal minPrecio, BigDecimal maxPrecio) {
        return productoRepository.findByPrecioRange(minPrecio, maxPrecio);
    }

    public List<Producto> obtenerConStock() {
        return productoRepository.findByStockGreaterThan(0);
    }

    public List<Producto> obtenerPorMarcaYCategoria(String marca, String categoria) {
        return productoRepository.findByMarcaAndCategoria(marca, categoria);
    }

    public List<Producto> obtenerDisponiblesYDestacados() {
        return productoRepository.findByDisponibleTrueAndEsDestacadoTrue();
    }

    // ========== MÉTODOS ALIAS (para compatibilidad) ==========

    public Optional<Producto> getProductoById(Long id) {
        return obtenerPorId(id);
    }

    public void deleteProducto(Long id) {
        eliminar(id);
    }
}
