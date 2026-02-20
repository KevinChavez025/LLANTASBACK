package com.haidainversiones.haidainversionesllantas.converter;

import com.haidainversiones.haidainversionesllantas.enums.EstadoPedido;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Convierte EstadoPedido enum <-> String para que Hibernate
 * pueda escribir/leer el tipo nativo PostgreSQL 'estado_pedido_enum'.
 *
 * Al usar @Converter(autoApply = true), se aplica automáticamente
 * a todos los campos de tipo EstadoPedido sin necesidad de anotarlos.
 */
@Converter(autoApply = true)
public class EstadoPedidoConverter implements AttributeConverter<EstadoPedido, String> {

    @Override
    public String convertToDatabaseColumn(EstadoPedido estado) {
        if (estado == null) return null;
        return estado.name(); // PENDIENTE, CONFIRMADO, etc.
    }

    @Override
    public EstadoPedido convertToEntityAttribute(String dbValue) {
        if (dbValue == null) return null;
        return EstadoPedido.valueOf(dbValue);
    }
}
