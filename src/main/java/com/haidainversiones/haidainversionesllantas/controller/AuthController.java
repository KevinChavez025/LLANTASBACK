package com.haidainversiones.haidainversionesllantas.controller;

import com.haidainversiones.haidainversionesllantas.dto.AuthResponse;
import com.haidainversiones.haidainversionesllantas.dto.LoginRequest;
import com.haidainversiones.haidainversionesllantas.dto.RegisterRequest;
import com.haidainversiones.haidainversionesllantas.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("=== LOGIN REQUEST ===");
            System.out.println("Email: " + loginRequest.getEmail());
            AuthResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("ERROR EN LOGIN: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        try {
            System.out.println("=== REGISTER REQUEST ===");
            System.out.println("Nombre: " + registerRequest.getNombre());
            System.out.println("Email: " + registerRequest.getEmail());
            System.out.println("Telefono: " + registerRequest.getTelefono());
            System.out.println("Direccion: " + registerRequest.getDireccion());

            AuthResponse response = authService.register(registerRequest);

            System.out.println("REGISTRO EXITOSO!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.out.println("ERROR EN REGISTRO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
