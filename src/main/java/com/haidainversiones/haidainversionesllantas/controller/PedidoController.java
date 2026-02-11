package com.haidainversiones.haidainversionesllantas.controller;

import com.haidainversiones.haidainversionesllantas.dto.CrearPedidoRequest;
import com.haidainversiones.haidainversionesllantas.entity.Pedido;
import com.haidainversiones.haidainversionesllantas.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // Crear pedido desde carrito
    @PostMapping
    public ResponseEntity<Pedido> crearPedido(@Valid @RequestBody CrearPedidoRequest request) {
        Pedido pedido = pedidoService.crearPedido(request);
        return ResponseEntity.ok(pedido);
    }

    // Ver todos los pedidos (admin)
    @GetMapping
    public ResponseEntity<List<Pedido>> obtenerTodos() {
        List<Pedido> pedidos = pedidoService.obtenerTodos();
        return ResponseEntity.ok(pedidos);
    }

    // Ver un pedido específico
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPorId(@PathVariable Long id) {
        return pedidoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Ver pedidos de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Pedido>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        List<Pedido> pedidos = pedidoService.obtenerPorUsuario(usuarioId);
        return ResponseEntity.ok(pedidos);
    }

    // Actualizar estado del pedido
    @PutMapping("/{id}/estado")
    public ResponseEntity<Pedido> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        Pedido pedido = pedidoService.actualizarEstado(id, estado);
        return ResponseEntity.ok(pedido);
    }
}
