package com.haidainversiones.haidainversionesllantas.service;

import com.haidainversiones.haidainversionesllantas.entity.CarritoItem;
import com.haidainversiones.haidainversionesllantas.entity.Producto;
import com.haidainversiones.haidainversionesllantas.repository.CarritoItemRepository;
import com.haidainversiones.haidainversionesllantas.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CarritoService {

    @Autowired
    private CarritoItemRepository carritoItemRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // Agregar producto al carrito (invitado)
    public CarritoItem agregarAlCarrito(String sessionId, Long productoId, Integer cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar si el producto ya está en el carrito
        Optional<CarritoItem> itemExistente = carritoItemRepository
                .findBySessionIdAndProductoId(sessionId, productoId);

        if (itemExistente.isPresent()) {
            // Si ya existe, actualizar cantidad
            CarritoItem item = itemExistente.get();
            item.setCantidad(item.getCantidad() + cantidad);
            return carritoItemRepository.save(item);
        } else {
            // Si no existe, crear nuevo item
            CarritoItem nuevoItem = new CarritoItem();
            nuevoItem.setSessionId(sessionId);
            nuevoItem.setProducto(producto);
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setPrecioUnitario(producto.getPrecio());
            return carritoItemRepository.save(nuevoItem);
        }
    }

    // Agregar producto al carrito (usuario registrado)
    public CarritoItem agregarAlCarritoUsuario(Long usuarioId, Long productoId, Integer cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Optional<CarritoItem> itemExistente = carritoItemRepository
                .findByUsuarioIdAndProductoId(usuarioId, productoId);

        if (itemExistente.isPresent()) {
            CarritoItem item = itemExistente.get();
            item.setCantidad(item.getCantidad() + cantidad);
            return carritoItemRepository.save(item);
        } else {
            CarritoItem nuevoItem = new CarritoItem();
            nuevoItem.getUsuario().setId(usuarioId);
            nuevoItem.setProducto(producto);
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setPrecioUnitario(producto.getPrecio());
            return carritoItemRepository.save(nuevoItem);
        }
    }

    // Obtener carrito (invitado)
    public List<CarritoItem> obtenerCarrito(String sessionId) {
        return carritoItemRepository.findBySessionId(sessionId);
    }

    // Obtener carrito (usuario registrado)
    public List<CarritoItem> obtenerCarritoUsuario(Long usuarioId) {
        return carritoItemRepository.findByUsuarioId(usuarioId);
    }

    // Actualizar cantidad
    public CarritoItem actualizarCantidad(Long itemId, Integer nuevaCantidad) {
        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));
        item.setCantidad(nuevaCantidad);
        return carritoItemRepository.save(item);
    }

    // Eliminar item del carrito
    public void eliminarItem(Long itemId) {
        carritoItemRepository.deleteById(itemId);
    }

    // Vaciar carrito (invitado)
    @Transactional
    public void vaciarCarrito(String sessionId) {
        carritoItemRepository.deleteBySessionId(sessionId);
    }

    // Vaciar carrito (usuario)
    @Transactional
    public void vaciarCarritoUsuario(Long usuarioId) {
        carritoItemRepository.deleteByUsuarioId(usuarioId);
    }

    // Calcular total del carrito
    public BigDecimal calcularTotal(List<CarritoItem> items) {
        return items.stream()
                .map(CarritoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
