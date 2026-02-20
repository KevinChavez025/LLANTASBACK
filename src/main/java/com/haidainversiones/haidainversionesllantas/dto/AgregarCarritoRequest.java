package com.haidainversiones.haidainversionesllantas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgregarCarritoRequest {

    private Long productoId;
    private Integer cantidad;
    private Long usuarioId;
}
