package com.haidainversiones.haidainversionesllantas.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@Data
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String marca;

    @Column(name = "tipo_vehiculo")
    private String tipoVehiculo; // Moto, Mototaxi, Camión, Minería, Auto, etc.

    private String medida; // 3.00-18, 4.00-8, 205/55R16, etc.

    private String categoria; // Delantero, Posterior, Mixta

    private BigDecimal precio;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private Integer stock;

    @Column(name = "disponible")
    private Boolean disponible = true;

    @Column(name = "es_nuevo")
    private Boolean esNuevo = false;

    @Column(name = "es_destacado")
    private Boolean esDestacado = false;

    @Column(name = "url_imagen")
    private String urlImagen;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
}
