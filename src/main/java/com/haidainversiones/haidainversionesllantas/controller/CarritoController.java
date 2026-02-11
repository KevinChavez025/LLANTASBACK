package com.haidainversiones.haidainversionesllantas.controller;

import com.haidainversiones.haidainversionesllantas.entity.CarritoItem;
import com.haidainversiones.haidainversionesllantas.service.CarritoService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
@Validated
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    // Agregar producto al carrito (invitado)
    @PostMapping("/agregar")
    public ResponseEntity<CarritoItem> agregarAlCarrito(
            @RequestParam String sessionId,
            @RequestParam @NotNull(message = "El ID del producto es obligatorio") Long productoId,
            @RequestParam @NotNull(message = "La cantidad es obligatoria") @Min(value = 1, message = "La cantidad debe ser al menos 1") Integer cantidad) {
        CarritoItem item = carritoService.agregarAlCarrito(sessionId, productoId, cantidad);
        return ResponseEntity.ok(item);
    }

    // Agregar producto al carrito (usuario registrado)
    @PostMapping("/agregar/usuario/{usuarioId}")
    public ResponseEntity<CarritoItem> agregarAlCarritoUsuario(
            @PathVariable Long usuarioId,
            @RequestParam @NotNull(message = "El ID del producto es obligatorio") Long productoId,
            @RequestParam @NotNull(message = "La cantidad es obligatoria") @Min(value = 1, message = "La cantidad debe ser al menos 1") Integer cantidad) {
        CarritoItem item = carritoService.agregarAlCarritoUsuario(usuarioId, productoId, cantidad);
        return ResponseEntity.ok(item);
    }

    // Ver carrito (invitado)
    @GetMapping
    public ResponseEntity<Map<String, Object>> verCarrito(@RequestParam String sessionId) {
        List<CarritoItem> items = carritoService.obtenerCarrito(sessionId);
        BigDecimal total = carritoService.calcularTotal(items);

        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        response.put("total", total);
        response.put("cantidadItems", items.size());

        return ResponseEntity.ok(response);
    }

    // Ver carrito (usuario registrado)
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Map<String, Object>> verCarritoUsuario(@PathVariable Long usuarioId) {
        List<CarritoItem> items = carritoService.obtenerCarritoUsuario(usuarioId);
        BigDecimal total = carritoService.calcularTotal(items);

        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        response.put("total", total);
        response.put("cantidadItems", items.size());

        return ResponseEntity.ok(response);
    }

    // Actualizar cantidad
    @PutMapping("/item/{itemId}")
    public ResponseEntity<CarritoItem> actualizarCantidad(
            @PathVariable Long itemId,
            @RequestParam @NotNull(message = "La cantidad es obligatoria") @Min(value = 1, message = "La cantidad debe ser al menos 1") Integer cantidad) {
        CarritoItem item = carritoService.actualizarCantidad(itemId, cantidad);
        return ResponseEntity.ok(item);
    }

    // Eliminar item
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Void> eliminarItem(@PathVariable Long itemId) {
        carritoService.eliminarItem(itemId);
        return ResponseEntity.ok().build();
    }

    // Vaciar carrito (invitado)
    @DeleteMapping("/vaciar")
    public ResponseEntity<Void> vaciarCarrito(@RequestParam String sessionId) {
        carritoService.vaciarCarrito(sessionId);
        return ResponseEntity.ok().build();
    }

    // Vaciar carrito (usuario)
    @DeleteMapping("/vaciar/usuario/{usuarioId}")
    public ResponseEntity<Void> vaciarCarritoUsuario(@PathVariable Long usuarioId) {
        carritoService.vaciarCarritoUsuario(usuarioId);
        return ResponseEntity.ok().build();
    }
}
