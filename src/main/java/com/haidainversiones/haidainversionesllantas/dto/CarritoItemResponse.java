package com.haidainversiones.haidainversionesllantas.dto;

import com.haidainversiones.haidainversionesllantas.entity.CarritoItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CarritoItemResponse {

    private Long id;
    private String sessionId;
    private Long usuarioId;

    // Datos del producto (aplanados — sin entidad anidada)
    private Long productoId;
    private String nombreProducto;
    private String marcaProducto;
    private String medidaProducto;
    private String urlImagenProducto;

    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private LocalDateTime fechaAgregado;

    /** Convierte una entidad CarritoItem a DTO de forma segura */
    public static CarritoItemResponse from(CarritoItem item) {
        CarritoItemResponse dto = new CarritoItemResponse();
        dto.setId(item.getId());
        dto.setSessionId(item.getSessionId());
        dto.setCantidad(item.getCantidad());
        dto.setPrecioUnitario(item.getPrecioUnitario());
        dto.setSubtotal(item.getSubtotal());
        dto.setFechaAgregado(item.getFechaAgregado());

        if (item.getUsuario() != null) {
            dto.setUsuarioId(item.getUsuario().getId());
        }
        if (item.getProducto() != null) {
            dto.setProductoId(item.getProducto().getId());
            dto.setNombreProducto(item.getProducto().getNombre());
            dto.setMarcaProducto(item.getProducto().getMarca());
            dto.setMedidaProducto(item.getProducto().getMedida());
            dto.setUrlImagenProducto(item.getProducto().getUrlImagen());
        }
        return dto;
    }
}