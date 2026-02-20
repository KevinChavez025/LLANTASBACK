-- ============================================================
-- V2: Tabla de auditoría de cambios de estado en pedidos
-- ============================================================
CREATE TABLE IF NOT EXISTS pedido_historial (
    id              BIGSERIAL          PRIMARY KEY,
    pedido_id       BIGINT             NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE,
    estado_anterior estado_pedido_enum,
    estado_nuevo    estado_pedido_enum NOT NULL,
    cambiado_por    VARCHAR(100),
    observacion     TEXT,
    fecha_cambio    TIMESTAMP          NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_historial_pedido      ON pedido_historial(pedido_id);
CREATE INDEX IF NOT EXISTS idx_historial_fecha_cambio ON pedido_historial(fecha_cambio DESC);
