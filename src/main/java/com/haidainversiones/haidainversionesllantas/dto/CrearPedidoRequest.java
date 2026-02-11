package com.haidainversiones.haidainversionesllantas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CrearPedidoRequest {

    // Para identificar el carrito
    @Size(max = 100, message = "El ID de sesión no puede exceder 100 caracteres")
    private String sessionId;

    private Long usuarioId;

    // Datos del cliente (para invitados - obligatorios si no hay usuarioId)
    @Size(max = 100, message = "El nombre del cliente no puede exceder 100 caracteres")
    private String nombreCliente;

    @Email(message = "El email del cliente debe ser válido")
    @Size(max = 100, message = "El email del cliente no puede exceder 100 caracteres")
    private String emailCliente;

    @Size(max = 20, message = "El teléfono del cliente no puede exceder 20 caracteres")
    private String telefonoCliente;

    // Datos de envío (obligatorios)
    @NotBlank(message = "La dirección de envío es obligatoria")
    @Size(max = 200, message = "La dirección de envío no puede exceder 200 caracteres")
    private String direccionEnvio;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    private String ciudad;

    @NotBlank(message = "El departamento es obligatorio")
    @Size(max = 100, message = "El departamento no puede exceder 100 caracteres")
    private String departamento;

    @Size(max = 10, message = "El código postal no puede exceder 10 caracteres")
    private String codigoPostal;

    // Datos adicionales (opcional)
    @Size(max = 500, message = "Las notas adicionales no pueden exceder 500 caracteres")
    private String notasAdicionales;
}
