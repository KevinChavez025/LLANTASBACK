package com.haidainversiones.haidainversionesllantas.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "roles")
@Data
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre; // ROLE_ADMIN, ROLE_VENDEDOR, ROLE_CLIENTE

    private String descripcion;
}
