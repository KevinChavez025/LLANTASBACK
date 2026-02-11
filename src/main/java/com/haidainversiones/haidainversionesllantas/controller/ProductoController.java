package com.haidainversiones.haidainversionesllantas.controller;

import com.haidainversiones.haidainversionesllantas.entity.Producto;
import com.haidainversiones.haidainversionesllantas.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @PostMapping
    public Producto createProducto(@RequestBody Producto producto) {
        return productoService.guardar(producto);
    }

    @GetMapping
    public List<Producto> getAllProductos() {
        return productoService.obtenerTodos();
    }

    // Obtener producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Long id) {
        return productoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar productos
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarProductos(@RequestParam String query) {
        List<Producto> productos = productoService.obtenerTodos().stream()
                .filter(p -> p.getNombre().toLowerCase().contains(query.toLowerCase()) ||
                        (p.getDescripcion() != null && p.getDescripcion().toLowerCase().contains(query.toLowerCase())) ||
                        (p.getMarca() != null && p.getMarca().toLowerCase().contains(query.toLowerCase())))
                .toList();
        return ResponseEntity.ok(productos);
    }

    // Obtener por marca
    @GetMapping("/marca/{marca}")
    public ResponseEntity<List<Producto>> getByMarca(@PathVariable String marca) {
        List<Producto> productos = productoService.obtenerPorMarca(marca);
        return ResponseEntity.ok(productos);
    }

    // Obtener por tipo de vehículo
    @GetMapping("/tipo-vehiculo/{tipo}")
    public ResponseEntity<List<Producto>> getByTipoVehiculo(@PathVariable String tipo) {
        List<Producto> productos = productoService.obtenerPorTipoVehiculo(tipo);
        return ResponseEntity.ok(productos);
    }

    // Obtener por medida
    @GetMapping("/medida/{medida}")
    public ResponseEntity<List<Producto>> getByMedida(@PathVariable String medida) {
        List<Producto> productos = productoService.obtenerPorMedida(medida);
        return ResponseEntity.ok(productos);
    }

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

    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(@PathVariable Long id, @RequestBody Producto productoDetails) {
        return productoService.getProductoById(id)
                .map(producto -> {
                    producto.setNombre(productoDetails.getNombre());
                    producto.setDescripcion(productoDetails.getDescripcion());
                    producto.setPrecio(productoDetails.getPrecio());
                    producto.setStock(productoDetails.getStock());
                    producto.setMarca(productoDetails.getMarca());
                    producto.setMedida(productoDetails.getMedida());
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        return productoService.getProductoById(id)
                .map(producto -> {
                    productoService.deleteProducto(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
