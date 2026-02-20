package com.haidainversiones.haidainversionesllantas.service;

import com.haidainversiones.haidainversionesllantas.dto.ProductoRequest;
import com.haidainversiones.haidainversionesllantas.entity.Producto;
import com.haidainversiones.haidainversionesllantas.exception.BadRequestException;
import com.haidainversiones.haidainversionesllantas.exception.ResourceNotFoundException;
import com.haidainversiones.haidainversionesllantas.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    // ===== CRUD BÁSICO =====

    public Page<Producto> obtenerTodosPaginado(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> getProductoById(Long id) {
        return productoRepository.findById(id);
    }

    public Producto obtenerPorIdOrThrow(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
    }

    @CacheEvict(value = "productos", allEntries = true)
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    @CacheEvict(value = "productos", allEntries = true)
    public Producto crearDesdeRequest(ProductoRequest request) {
        Producto producto = mapearRequest(request, new Producto());
        return productoRepository.save(producto);
    }

    @CacheEvict(value = "productos", allEntries = true)
    public Producto actualizarDesdeRequest(Long id, ProductoRequest request) {
        Producto producto = obtenerPorIdOrThrow(id);
        mapearRequest(request, producto);
        return productoRepository.save(producto);
    }

    private Producto mapearRequest(ProductoRequest request, Producto producto) {
        producto.setNombre(request.getNombre());
        producto.setMarca(request.getMarca());
        producto.setModelo(request.getModelo());
        producto.setTipoVehiculo(request.getTipoVehiculo());
        producto.setMedida(request.getMedida());
        producto.setCategoria(request.getCategoria());
        producto.setPrecio(request.getPrecio());
        producto.setDescripcion(request.getDescripcion());
        producto.setStock(request.getStock() != null ? request.getStock() : 0);
        producto.setDisponible(request.getDisponible() != null ? request.getDisponible() : true);
        producto.setEsNuevo(request.getEsNuevo() != null ? request.getEsNuevo() : false);
        producto.setEsDestacado(request.getEsDestacado() != null ? request.getEsDestacado() : false);
        producto.setUrlImagen(request.getUrlImagen());
        return producto;
    }

    /**
     * Soft delete: marca el producto como no disponible.
     * Nunca borra físicamente si tiene pedidos históricos (protege FKs).
     */
    @CacheEvict(value = "productos", allEntries = true)
    public void eliminar(Long id) {
        Producto producto = obtenerPorIdOrThrow(id);
        producto.setDisponible(false);
        producto.setStock(0);
        productoRepository.save(producto);
    }

    /**
     * Elimina físicamente solo si no hay detalle_pedidos referenciando el producto.
     */
    @CacheEvict(value = "productos", allEntries = true)
    public void eliminarFisico(Long id) {
        Producto producto = obtenerPorIdOrThrow(id);
        if (productoRepository.tieneDetallesPedido(id)) {
            throw new BadRequestException(
                "No se puede eliminar el producto '" + producto.getNombre() +
                "' porque tiene pedidos históricos. Use la opción de desactivar."
            );
        }
        productoRepository.deleteById(id);
    }

    @CacheEvict(value = "productos", allEntries = true)
    public void deleteProducto(Long id) {
        productoRepository.deleteById(id);
    }

    // ===== FILTROS =====

    @Cacheable("productos")
    public List<Producto> obtenerDestacados()        { return productoRepository.findByEsDestacadoTrue(); }

    @Cacheable("productos")
    public List<Producto> obtenerNuevos()            { return productoRepository.findByEsNuevoTrue(); }

    @Cacheable("productos")
    public List<Producto> obtenerDisponibles()       { return productoRepository.findByDisponibleTrue(); }

    public List<Producto> obtenerPorMarca(String marca)         { return productoRepository.findByMarca(marca); }
    public List<Producto> obtenerPorModelo(String modelo)       { return productoRepository.findByModelo(modelo); }
    public List<Producto> obtenerPorTipoVehiculo(String tipo)   { return productoRepository.findByTipoVehiculo(tipo); }
    public List<Producto> obtenerPorMedida(String medida)       { return productoRepository.findByMedida(medida); }
    public List<Producto> obtenerPorCategoria(String categoria) { return productoRepository.findByCategoria(categoria); }
    public List<Producto> buscarPorTexto(String query)          { return productoRepository.buscarPorTexto(query); }
    public List<Producto> obtenerRecientes()                    { return productoRepository.findTop10ByOrderByFechaCreacionDesc(); }
    public List<Producto> obtenerPorRangoPrecio(BigDecimal min, BigDecimal max) { return productoRepository.findByPrecioRange(min, max); }
    public List<Producto> obtenerConStock()                     { return productoRepository.findByStockGreaterThan(0); }
    public List<Producto> obtenerDisponiblesYDestacados()       { return productoRepository.findByDisponibleTrueAndEsDestacadoTrue(); }
}
