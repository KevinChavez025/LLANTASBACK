package com.haidainversiones.haidainversionesllantas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@Data
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 100, message = "La marca no puede exceder 100 caracteres")
    private String marca;

    @Size(max = 100, message = "El modelo no puede exceder 100 caracteres")
    private String modelo;

    @Size(max = 50, message = "El tipo de vehículo no puede exceder 50 caracteres")
    @Column(name = "tipo_vehiculo")
    private String tipoVehiculo; // Moto, Mototaxi, Camión, Minería, Auto, etc.

    @Size(max = 50, message = "La medida no puede exceder 50 caracteres")
    private String medida; // 3.00-18, 4.00-8, 205/55R16, etc.

    @Size(max = 50, message = "La categoría no puede exceder 50 caracteres")
    private String categoria; // Delantero, Posterior, Mixta

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener máximo 10 enteros y 2 decimales")
    private BigDecimal precio;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // Stock con control de concurrencia para evitar overselling
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock = 0;

    @Column(name = "disponible")
    private Boolean disponible = true;

    @Column(name = "es_nuevo")
    private Boolean esNuevo = false;

    @Column(name = "es_destacado")
    private Boolean esDestacado = false;

    @Size(max = 500, message = "La URL de imagen no puede exceder 500 caracteres")
    @Column(name = "url_imagen")
    private String urlImagen;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    /**
     * Descuenta stock de forma segura. Lanza excepción si no hay suficiente.
     */
    public void descontarStock(int cantidad) {
        if (this.stock < cantidad) {
            throw new IllegalStateException(
                "Stock insuficiente para el producto '" + this.nombre +
                "'. Disponible: " + this.stock + ", solicitado: " + cantidad
            );
        }
        this.stock -= cantidad;
        // Si el stock llega a 0, marcar como no disponible automáticamente
        if (this.stock == 0) {
            this.disponible = false;
        }
    }
}
