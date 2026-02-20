package com.haidainversiones.haidainversionesllantas.dto;

import lombok.Data;

/**
 * Respuesta de autenticación.
 * Incluye access token (corta duración) y refresh token (larga duración).
 */
@Data
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String nombre;
    private String rol;

    /** Constructor para login/register completo (con refresh token) */
    public AuthResponse(String accessToken, String refreshToken,
                        Long id, String email, String nombre, String rol) {
        this.accessToken  = accessToken;
        this.refreshToken = refreshToken;
        this.id      = id;
        this.email   = email;
        this.username = email;
        this.nombre  = nombre;
        this.rol     = rol;
    }

    /** Constructor para refresh (solo nuevo access token, mismo refresh token) */
    public AuthResponse(String accessToken, String refreshToken) {
        this.accessToken  = accessToken;
        this.refreshToken = refreshToken;
    }
}
