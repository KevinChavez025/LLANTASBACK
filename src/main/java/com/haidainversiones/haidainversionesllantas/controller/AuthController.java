package com.haidainversiones.haidainversionesllantas.controller;

import com.haidainversiones.haidainversionesllantas.dto.AuthResponse;
import com.haidainversiones.haidainversionesllantas.dto.LoginRequest;
import com.haidainversiones.haidainversionesllantas.dto.RefreshTokenRequest;
import com.haidainversiones.haidainversionesllantas.dto.RegisterRequest;
import com.haidainversiones.haidainversionesllantas.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    /**
     * Renueva el access token usando el refresh token.
     * El frontend debe llamar a este endpoint cuando reciba TOKEN_EXPIRED (401).
     * Flujo:
     *   1. Frontend recibe 401 TOKEN_EXPIRED
     *   2. Llama POST /api/auth/refresh con el refreshToken guardado
     *   3. Recibe nuevo accessToken + refreshToken (rotación)
     *   4. Reintenta el request original con el nuevo accessToken
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }

    /**
     * Logout: revoca todos los refresh tokens del usuario.
     * El access token expirará solo (vida corta de 15 min).
     */
    @PostMapping("/logout/{usuarioId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> logout(@PathVariable Long usuarioId) {
        authService.logout(usuarioId);
        return ResponseEntity.ok(Map.of("message", "Sesión cerrada correctamente"));
    }
}
