package com.haidainversiones.haidainversionesllantas.converter;

import com.haidainversiones.haidainversionesllantas.enums.EstadoPago;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EstadoPagoConverter implements AttributeConverter<EstadoPago, String> {

    @Override
    public String convertToDatabaseColumn(EstadoPago estado) {
        if (estado == null) return null;
        return estado.name();
    }

    @Override
    public EstadoPago convertToEntityAttribute(String dbValue) {
        if (dbValue == null) return null;
        return EstadoPago.valueOf(dbValue);
    }
}
