package com.haidainversiones.haidainversionesllantas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CrearPedidoRequest {

    /**
     * Clave única generada por el frontend al abrir el checkout (UUID).
     * Protege contra doble submit: si el mismo key llega dos veces,
     * el servidor retorna el pedido original sin crear uno nuevo ni cobrar dos veces.
     *
     * El frontend debe generar este valor UNA SOLA VEZ por intento de compra
     * y enviarlo siempre en el mismo request (incluso en reintentos por timeout).
     */
    @Size(max = 100)
    private String idempotencyKey;

    // Para usuarios registrados
    private Long usuarioId;

    // Para usuarios invitados
    private String sessionId;

    // ===== DATOS DEL CLIENTE (para invitados) =====
    @Size(max = 100)
    private String nombreCliente;

    @Email
    @Size(max = 100)
    private String emailCliente;

    @Size(max = 20)
    private String telefonoCliente;

    // ===== DIRECCIÓN DE ENVÍO =====
    @NotBlank(message = "La dirección de envío es obligatoria")
    @Size(max = 200)
    private String direccionEnvio;

    @Size(max = 100)
    private String ciudad;

    @Size(max = 100)
    private String distrito;

    @Size(max = 100)
    private String departamento;

    @Size(max = 10)
    private String codigoPostal;

    @Size(max = 20)
    private String telefonoContacto;

    // ===== PAGO =====
    // Valores válidos: EFECTIVO, TARJETA_CREDITO, TARJETA_DEBITO, TRANSFERENCIA, YAPE, PLIN
    private String metodoPago;

    // ===== NOTAS =====
    @Size(max = 500)
    private String notasAdicionales;
}
