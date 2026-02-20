package com.haidainversiones.haidainversionesllantas.controller;

import com.haidainversiones.haidainversionesllantas.dto.CarritoItemResponse;
import com.haidainversiones.haidainversionesllantas.entity.CarritoItem;
import com.haidainversiones.haidainversionesllantas.service.CarritoService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
@Validated
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    // ===== CARRITO INVITADO (sessionId) =====

    @PostMapping("/agregar")
    @Transactional
    public ResponseEntity<CarritoItemResponse> agregarAlCarrito(
            @RequestParam String sessionId,
            @RequestParam @NotNull Long productoId,
            @RequestParam @NotNull @Min(1) Integer cantidad) {
        CarritoItem item = carritoService.agregarAlCarrito(sessionId, productoId, cantidad);
        return ResponseEntity.ok(CarritoItemResponse.from(item));
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> verCarrito(@RequestParam String sessionId) {
        List<CarritoItem> items = carritoService.obtenerCarrito(sessionId);
        BigDecimal total = carritoService.calcularTotal(items);
        List<CarritoItemResponse> dtos = items.stream().map(CarritoItemResponse::from).toList();
        Map<String, Object> response = new HashMap<>();
        response.put("items", dtos);
        response.put("total", total);
        response.put("cantidadItems", dtos.size());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/vaciar")
    public ResponseEntity<Void> vaciarCarrito(@RequestParam String sessionId) {
        carritoService.vaciarCarrito(sessionId);
        return ResponseEntity.noContent().build();
    }

    // ===== CARRITO USUARIO REGISTRADO =====

    @PostMapping("/agregar/usuario/{usuarioId}")
    @Transactional
    public ResponseEntity<CarritoItemResponse> agregarAlCarritoUsuario(
            @PathVariable Long usuarioId,
            @RequestParam @NotNull Long productoId,
            @RequestParam @NotNull @Min(1) Integer cantidad) {
        CarritoItem item = carritoService.agregarAlCarritoUsuario(usuarioId, productoId, cantidad);
        return ResponseEntity.ok(CarritoItemResponse.from(item));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> verCarritoUsuario(@PathVariable Long usuarioId) {
        List<CarritoItem> items = carritoService.obtenerCarritoUsuario(usuarioId);
        BigDecimal total = carritoService.calcularTotal(items);
        List<CarritoItemResponse> dtos = items.stream().map(CarritoItemResponse::from).toList();
        Map<String, Object> response = new HashMap<>();
        response.put("items", dtos);
        response.put("total", total);
        response.put("cantidadItems", dtos.size());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/vaciar/usuario/{usuarioId}")
    public ResponseEntity<Void> vaciarCarritoUsuario(@PathVariable Long usuarioId) {
        carritoService.vaciarCarritoUsuario(usuarioId);
        return ResponseEntity.noContent().build();
    }

    // ===== OPERACIONES COMPARTIDAS =====

    @PutMapping("/item/{itemId}")
    @Transactional
    public ResponseEntity<CarritoItemResponse> actualizarCantidad(
            @PathVariable Long itemId,
            @RequestParam @NotNull @Min(1) Integer cantidad) {
        CarritoItem item = carritoService.actualizarCantidad(itemId, cantidad);
        return ResponseEntity.ok(CarritoItemResponse.from(item));
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Void> eliminarItem(@PathVariable Long itemId) {
        carritoService.eliminarItem(itemId);
        return ResponseEntity.noContent().build();
    }
}