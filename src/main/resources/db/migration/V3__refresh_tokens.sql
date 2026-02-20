-- ============================================================
-- V3: Tabla de refresh tokens para JWT de larga duración
-- ============================================================
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id             BIGSERIAL    PRIMARY KEY,
    usuario_id     BIGINT       NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    token          VARCHAR(512) NOT NULL UNIQUE,
    fecha_expira   TIMESTAMP    NOT NULL,
    revocado       BOOLEAN      NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_refresh_token_value   ON refresh_tokens(token);
CREATE INDEX IF NOT EXISTS idx_refresh_token_usuario ON refresh_tokens(usuario_id);
-- Índice parcial: solo tokens activos (no revocados), que son los que se buscan
CREATE INDEX IF NOT EXISTS idx_refresh_token_activos ON refresh_tokens(token) WHERE revocado = FALSE;
