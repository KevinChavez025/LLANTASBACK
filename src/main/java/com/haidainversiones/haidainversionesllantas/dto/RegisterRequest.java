package com.haidainversiones.haidainversionesllantas.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nombre;
    private String email;
    private String password;
    private String telefono;
    private String direccion;
}
