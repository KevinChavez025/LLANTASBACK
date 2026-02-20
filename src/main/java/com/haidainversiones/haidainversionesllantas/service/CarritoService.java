package com.haidainversiones.haidainversionesllantas.service;

import com.haidainversiones.haidainversionesllantas.entity.CarritoItem;
import com.haidainversiones.haidainversionesllantas.entity.Producto;
import com.haidainversiones.haidainversionesllantas.entity.Usuario;
import com.haidainversiones.haidainversionesllantas.exception.BadRequestException;
import com.haidainversiones.haidainversionesllantas.exception.ResourceNotFoundException;
import com.haidainversiones.haidainversionesllantas.repository.CarritoItemRepository;
import com.haidainversiones.haidainversionesllantas.repository.ProductoRepository;
import com.haidainversiones.haidainversionesllantas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoItemRepository carritoItemRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    // ===== CARRITO INVITADO (sessionId) =====

    public CarritoItem agregarAlCarrito(String sessionId, Long productoId, Integer cantidad) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new BadRequestException("El sessionId es obligatorio para usuarios invitados");
        }

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", productoId));

        if (Boolean.FALSE.equals(producto.getDisponible())) {
            throw new BadRequestException("El producto no está disponible");
        }

        Optional<CarritoItem> itemExistente = carritoItemRepository
                .findBySessionIdAndProductoId(sessionId, productoId);

        if (itemExistente.isPresent()) {
            CarritoItem item = itemExistente.get();
            item.setCantidad(item.getCantidad() + cantidad);
            return carritoItemRepository.save(item);
        } else {
            CarritoItem nuevoItem = new CarritoItem();
            nuevoItem.setSessionId(sessionId);
            nuevoItem.setProducto(producto);
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setPrecioUnitario(producto.getPrecio());
            return carritoItemRepository.save(nuevoItem);
        }
    }

    public List<CarritoItem> obtenerCarrito(String sessionId) {
        return carritoItemRepository.findBySessionId(sessionId);
    }

    @Transactional
    public void vaciarCarrito(String sessionId) {
        carritoItemRepository.deleteBySessionId(sessionId);
    }

    // ===== CARRITO USUARIO REGISTRADO =====

    public CarritoItem agregarAlCarritoUsuario(Long usuarioId, Long productoId, Integer cantidad) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", productoId));

        if (Boolean.FALSE.equals(producto.getDisponible())) {
            throw new BadRequestException("El producto no está disponible");
        }

        Optional<CarritoItem> itemExistente = carritoItemRepository
                .findByUsuarioIdAndProductoId(usuarioId, productoId);

        if (itemExistente.isPresent()) {
            CarritoItem item = itemExistente.get();
            item.setCantidad(item.getCantidad() + cantidad);
            return carritoItemRepository.save(item);
        } else {
            CarritoItem nuevoItem = new CarritoItem();
            nuevoItem.setUsuario(usuario);
            nuevoItem.setProducto(producto);
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setPrecioUnitario(producto.getPrecio());
            return carritoItemRepository.save(nuevoItem);
        }
    }

    public List<CarritoItem> obtenerCarritoUsuario(Long usuarioId) {
        return carritoItemRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public void vaciarCarritoUsuario(Long usuarioId) {
        carritoItemRepository.deleteByUsuarioId(usuarioId);
    }

    // ===== OPERACIONES COMPARTIDAS =====

    public CarritoItem actualizarCantidad(Long itemId, Integer nuevaCantidad) {
        if (nuevaCantidad < 1) {
            throw new BadRequestException("La cantidad debe ser al menos 1");
        }
        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item de carrito", "id", itemId));
        item.setCantidad(nuevaCantidad);
        return carritoItemRepository.save(item);
    }

    public void eliminarItem(Long itemId) {
        if (!carritoItemRepository.existsById(itemId)) {
            throw new ResourceNotFoundException("Item de carrito", "id", itemId);
        }
        carritoItemRepository.deleteById(itemId);
    }

    public BigDecimal calcularTotal(List<CarritoItem> items) {
        return items.stream()
                .map(CarritoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
