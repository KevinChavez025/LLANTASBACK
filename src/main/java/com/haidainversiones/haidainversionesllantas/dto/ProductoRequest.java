package com.haidainversiones.haidainversionesllantas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO para crear y actualizar productos desde el admin.
 * Evita exponer la entidad directamente y previene que el cliente
 * manipule campos internos como fechaCreacion, id, etc.
 */
@Data
public class ProductoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres")
    private String nombre;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 100, message = "La marca no puede exceder 100 caracteres")
    private String marca;

    @Size(max = 100, message = "El modelo no puede exceder 100 caracteres")
    private String modelo;

    @Size(max = 50, message = "El tipo de vehículo no puede exceder 50 caracteres")
    private String tipoVehiculo;

    @Size(max = 50, message = "La medida no puede exceder 50 caracteres")
    private String medida;

    @Size(max = 50, message = "La categoría no puede exceder 50 caracteres")
    private String categoria;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal precio;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    private Boolean disponible = true;
    private Boolean esNuevo = false;
    private Boolean esDestacado = false;

    @Size(max = 500)
    private String urlImagen;
}
