package com.haidainversiones.haidainversionesllantas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para pedidos.
 * Evita serializar la entidad JPA directamente (previene LazyInitializationException
 * y exposición de datos sensibles del usuario).
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PedidoResponse {

    private Long id;
    private String numeroPedido;
    private String estado;
    private String metodoPago;
    private String estadoPago;

    // Montos
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal costoEnvio;
    private BigDecimal total;

    // Dirección
    private String direccionEnvio;
    private String ciudadEnvio;
    private String distritoEnvio;
    private String departamentoEnvio;
    private String codigoPostalEnvio;
    private String telefonoContacto;

    // Datos del cliente
    private Long usuarioId;
    private String emailCliente;
    private String nombreCliente;

    // Detalles
    private List<DetallePedidoDTO> detalles;

    // Fechas
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEntregaEstimada;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetallePedidoDTO {
        private Long id;
        private Long productoId;
        private String nombreProducto;
        private String marcaProducto;
        private String medidaProducto;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
    }
}
