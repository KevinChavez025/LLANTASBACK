package com.haidainversiones.haidainversionesllantas.controller;

import com.haidainversiones.haidainversionesllantas.entity.Producto;
import com.haidainversiones.haidainversionesllantas.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // ========== CRUD BÁSICO ==========

    // Crear producto
    @PostMapping
    public ResponseEntity<Producto> createProducto(@RequestBody Producto producto) {
        Producto nuevoProducto = productoService.guardar(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    // Obtener todos los productos
    @GetMapping
    public ResponseEntity<List<Producto>> getAllProductos() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    // Obtener producto por ID
// Obtener producto por ID (✅ MEJORADO con manejo de excepciones)
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Long id) {
        Producto producto = productoService.obtenerPorIdOrThrow(id);
        return ResponseEntity.ok(producto);
    }


    // Actualizar producto
    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(
            @PathVariable Long id,
            @Valid @RequestBody Producto productoDetails) {
        return productoService.getProductoById(id)
                .map(producto -> {
                    producto.setNombre(productoDetails.getNombre());
                    producto.setDescripcion(productoDetails.getDescripcion());
                    producto.setPrecio(productoDetails.getPrecio());
                    producto.setStock(productoDetails.getStock());
                    producto.setMarca(productoDetails.getMarca());
                    producto.setModelo(productoDetails.getModelo());
                    producto.setMedida(productoDetails.getMedida());
                    producto.setCategoria(productoDetails.getCategoria());
                    producto.setTipoVehiculo(productoDetails.getTipoVehiculo());
                    producto.setUrlImagen(productoDetails.getUrlImagen());
                    producto.setDisponible(productoDetails.getDisponible());
                    producto.setEsNuevo(productoDetails.getEsNuevo());
                    producto.setEsDestacado(productoDetails.getEsDestacado());
                    Producto updatedProducto = productoService.guardar(producto);
                    return ResponseEntity.ok(updatedProducto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        return productoService.getProductoById(id)
                .map(producto -> {
                    productoService.deleteProducto(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ========== BÚSQUEDA Y FILTROS ==========

    // Buscar productos por texto
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarProductos(@RequestParam String query) {
        List<Producto> productos = productoService.buscarPorTexto(query);
        return ResponseEntity.ok(productos);
    }

    // Filtrar por categoría
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Producto>> getByCategoria(@PathVariable String categoria) {
        List<Producto> productos = productoService.obtenerPorCategoria(categoria);
        return ResponseEntity.ok(productos);
    }

    // Filtrar por marca
    @GetMapping("/marca/{marca}")
    public ResponseEntity<List<Producto>> getByMarca(@PathVariable String marca) {
        List<Producto> productos = productoService.obtenerPorMarca(marca);
        return ResponseEntity.ok(productos);
    }

    // ✅ NUEVO: Filtrar por modelo
    @GetMapping("/modelo/{modelo}")
    public ResponseEntity<List<Producto>> getByModelo(@PathVariable String modelo) {
        List<Producto> productos = productoService.obtenerPorModelo(modelo);
        return ResponseEntity.ok(productos);
    }

    // Filtrar por tipo de vehículo
    @GetMapping("/tipo-vehiculo/{tipo}")
    public ResponseEntity<List<Producto>> getByTipoVehiculo(@PathVariable String tipo) {
        List<Producto> productos = productoService.obtenerPorTipoVehiculo(tipo);
        return ResponseEntity.ok(productos);
    }

    // Filtrar por medida
    @GetMapping("/medida/{medida}")
    public ResponseEntity<List<Producto>> getByMedida(@PathVariable String medida) {
        List<Producto> productos = productoService.obtenerPorMedida(medida);
        return ResponseEntity.ok(productos);
    }

    // ========== ENDPOINTS ESPECIALES ==========

    // Obtener productos destacados
    @GetMapping("/destacados")
    public ResponseEntity<List<Producto>> getDestacados() {
        List<Producto> productos = productoService.obtenerDestacados();
        return ResponseEntity.ok(productos);
    }

    // Obtener productos nuevos
    @GetMapping("/nuevos")
    public ResponseEntity<List<Producto>> getNuevos() {
        List<Producto> productos = productoService.obtenerNuevos();
        return ResponseEntity.ok(productos);
    }

    // Obtener productos disponibles
    @GetMapping("/disponibles")
    public ResponseEntity<List<Producto>> getDisponibles() {
        List<Producto> productos = productoService.obtenerDisponibles();
        return ResponseEntity.ok(productos);
    }

    // Obtener productos más recientes
    @GetMapping("/recientes")
    public ResponseEntity<List<Producto>> getRecientes() {
        List<Producto> productos = productoService.obtenerRecientes();
        return ResponseEntity.ok(productos);
    }

    // Buscar por rango de precio (✅ CORREGIDO: BigDecimal)
    @GetMapping("/precio")
    public ResponseEntity<List<Producto>> getByRangoPrecio(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        List<Producto> productos = productoService.obtenerPorRangoPrecio(min, max);
        return ResponseEntity.ok(productos);
    }

    // Obtener productos con stock
    @GetMapping("/con-stock")
    public ResponseEntity<List<Producto>> getConStock() {
        List<Producto> productos = productoService.obtenerConStock();
        return ResponseEntity.ok(productos);
    }

    // Productos disponibles y destacados
    @GetMapping("/disponibles-destacados")
    public ResponseEntity<List<Producto>> getDisponiblesYDestacados() {
        List<Producto> productos = productoService.obtenerDisponiblesYDestacados();
        return ResponseEntity.ok(productos);
    }
}
