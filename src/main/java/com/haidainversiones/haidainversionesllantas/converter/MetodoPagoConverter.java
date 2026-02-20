package com.haidainversiones.haidainversionesllantas.converter;

import com.haidainversiones.haidainversionesllantas.enums.MetodoPago;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MetodoPagoConverter implements AttributeConverter<MetodoPago, String> {

    @Override
    public String convertToDatabaseColumn(MetodoPago metodo) {
        if (metodo == null) return null;
        return metodo.name();
    }

    @Override
    public MetodoPago convertToEntityAttribute(String dbValue) {
        if (dbValue == null) return null;
        return MetodoPago.valueOf(dbValue);
    }
}
