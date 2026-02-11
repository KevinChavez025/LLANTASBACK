package com.haidainversiones.haidainversionesllantas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarritoResponse {

    private Long usuarioId;
    private List<ItemCarritoDTO> items;
    private BigDecimal subtotal;
    private BigDecimal total;
    private Integer cantidadItems;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemCarritoDTO {
        private Long id;
        private Long productoId;
        private String nombreProducto;
        private String marcaProducto;
        private String modeloProducto;
        private BigDecimal precioUnitario;
        private Integer cantidad;
        private BigDecimal subtotal;
        private String imagenUrl;
    }
}
