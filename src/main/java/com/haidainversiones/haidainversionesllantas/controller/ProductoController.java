package com.haidainversiones.haidainversionesllantas.controller;

import com.haidainversiones.haidainversionesllantas.dto.ProductoRequest;
import com.haidainversiones.haidainversionesllantas.entity.Producto;
import com.haidainversiones.haidainversionesllantas.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // ===== CRUD (requiere ADMIN según SecurityConfig) =====

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> crear(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crearDesdeRequest(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id,
                                               @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.actualizarDesdeRequest(id, request));
    }

    /** Soft delete: desactiva el producto (seguro con pedidos históricos) */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /** Hard delete: elimina físicamente solo si no tiene pedidos */
    @DeleteMapping("/{id}/fisico")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarFisico(@PathVariable Long id) {
        productoService.eliminarFisico(id);
        return ResponseEntity.noContent().build();
    }

    // ===== LECTURA (públicos) =====

    @GetMapping
    public ResponseEntity<Page<Producto>> getAll(
            @PageableDefault(size = 20, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(productoService.obtenerTodosPaginado(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorIdOrThrow(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscar(@RequestParam String q) {
        return ResponseEntity.ok(productoService.buscarPorTexto(q));
    }

    @GetMapping("/destacados")
    public ResponseEntity<List<Producto>> getDestacados() {
        return ResponseEntity.ok(productoService.obtenerDestacados());
    }

    @GetMapping("/nuevos")
    public ResponseEntity<List<Producto>> getNuevos() {
        return ResponseEntity.ok(productoService.obtenerNuevos());
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<Producto>> getDisponibles() {
        return ResponseEntity.ok(productoService.obtenerDisponibles());
    }

    @GetMapping("/recientes")
    public ResponseEntity<List<Producto>> getRecientes() {
        return ResponseEntity.ok(productoService.obtenerRecientes());
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Producto>> getByCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(productoService.obtenerPorCategoria(categoria));
    }

    @GetMapping("/marca/{marca}")
    public ResponseEntity<List<Producto>> getByMarca(@PathVariable String marca) {
        return ResponseEntity.ok(productoService.obtenerPorMarca(marca));
    }

    @GetMapping("/tipo-vehiculo/{tipo}")
    public ResponseEntity<List<Producto>> getByTipoVehiculo(@PathVariable String tipo) {
        return ResponseEntity.ok(productoService.obtenerPorTipoVehiculo(tipo));
    }

    @GetMapping("/medida/{medida}")
    public ResponseEntity<List<Producto>> getByMedida(@PathVariable String medida) {
        return ResponseEntity.ok(productoService.obtenerPorMedida(medida));
    }

    @GetMapping("/precio")
    public ResponseEntity<List<Producto>> getByRangoPrecio(@RequestParam BigDecimal min,
                                                            @RequestParam BigDecimal max) {
        return ResponseEntity.ok(productoService.obtenerPorRangoPrecio(min, max));
    }

    @GetMapping("/con-stock")
    public ResponseEntity<List<Producto>> getConStock() {
        return ResponseEntity.ok(productoService.obtenerConStock());
    }
}
