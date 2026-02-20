package com.haidainversiones.haidainversionesllantas.controller;

import com.haidainversiones.haidainversionesllantas.dto.CrearPedidoRequest;
import com.haidainversiones.haidainversionesllantas.dto.PedidoResponse;
import com.haidainversiones.haidainversionesllantas.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponse> crearPedido(
            @Valid @RequestBody CrearPedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoService.crearPedido(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PedidoResponse>> obtenerTodos(
            @PageableDefault(size = 20, sort = "fechaCreacion",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(pedidoService.obtenerTodosPaginado(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }

    // ✅ NUEVO — Usuario ve su propio pedido por ID
    @GetMapping("/mis-pedidos/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PedidoResponse> obtenerMiPedido(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        boolean esAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Si no es admin, verificar que el pedido le pertenece
        if (!esAdmin) {
            pedidoService.verificarOwnershipPedido(id, userDetails.getUsername());
        }

        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PedidoResponse>> obtenerPorUsuario(
            @PathVariable Long usuarioId,
            @AuthenticationPrincipal UserDetails userDetails) {

        boolean esAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!esAdmin) {
            pedidoService.verificarOwnership(usuarioId, userDetails.getUsername());
        }

        return ResponseEntity.ok(pedidoService.obtenerPorUsuario(usuarioId));
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoResponse> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, body.get("estado")));
    }

    @GetMapping("/{id}/historial")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> obtenerHistorial(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerHistorial(id));
    }
}
