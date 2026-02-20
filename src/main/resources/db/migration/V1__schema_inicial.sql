-- ============================================================
-- V1: Schema inicial — Haida Inversiones Llantas
-- ============================================================

-- ===== TIPOS ENUM NATIVOS DE POSTGRESQL =====

DO $$ BEGIN
    CREATE TYPE estado_pedido_enum AS ENUM (
        'PENDIENTE', 'CONFIRMADO', 'EN_PREPARACION', 'ENVIADO', 'ENTREGADO', 'CANCELADO'
    );
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE estado_pago_enum AS ENUM (
        'PENDIENTE', 'PAGADO', 'FALLIDO', 'REEMBOLSADO'
    );
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE metodo_pago_enum AS ENUM (
        'EFECTIVO', 'TARJETA_CREDITO', 'TARJETA_DEBITO', 'TRANSFERENCIA', 'YAPE', 'PLIN'
    );
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- ===== TABLAS =====

CREATE TABLE IF NOT EXISTS roles (
    id          BIGSERIAL    PRIMARY KEY,
    nombre      VARCHAR(50)  NOT NULL UNIQUE,
    descripcion VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS usuarios (
    id                  BIGSERIAL    PRIMARY KEY,
    username            VARCHAR(50)  NOT NULL UNIQUE,
    email               VARCHAR(100) NOT NULL UNIQUE,
    password            VARCHAR(255) NOT NULL,
    nombre_completo     VARCHAR(100) NOT NULL,
    telefono            VARCHAR(20),
    direccion           VARCHAR(200),
    ciudad              VARCHAR(100),
    distrito            VARCHAR(100),
    codigo_postal       VARCHAR(10),
    notas               TEXT,
    activo              BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_creacion      TIMESTAMP    NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP,
    ultimo_acceso       TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_usuarios_email    ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_usuarios_username ON usuarios(username);

CREATE TABLE IF NOT EXISTS usuario_roles (
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    rol_id     BIGINT NOT NULL REFERENCES roles(id)    ON DELETE CASCADE,
    PRIMARY KEY (usuario_id, rol_id)
);

CREATE TABLE IF NOT EXISTS productos (
    id                  BIGSERIAL     PRIMARY KEY,
    nombre              VARCHAR(200)  NOT NULL,
    marca               VARCHAR(100)  NOT NULL,
    modelo              VARCHAR(100),
    tipo_vehiculo       VARCHAR(50),
    medida              VARCHAR(50),
    categoria           VARCHAR(50),
    precio              NUMERIC(10,2) NOT NULL CHECK (precio > 0),
    descripcion         TEXT,
    stock               INTEGER       NOT NULL DEFAULT 0 CHECK (stock >= 0),
    disponible          BOOLEAN       NOT NULL DEFAULT TRUE,
    es_nuevo            BOOLEAN       NOT NULL DEFAULT FALSE,
    es_destacado        BOOLEAN       NOT NULL DEFAULT FALSE,
    url_imagen          VARCHAR(500),
    fecha_creacion      TIMESTAMP     NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_productos_marca         ON productos(marca);
CREATE INDEX IF NOT EXISTS idx_productos_tipo_vehiculo ON productos(tipo_vehiculo);
CREATE INDEX IF NOT EXISTS idx_productos_disponible    ON productos(disponible);
CREATE INDEX IF NOT EXISTS idx_productos_es_destacado  ON productos(es_destacado);
CREATE INDEX IF NOT EXISTS idx_productos_medida        ON productos(medida);
CREATE INDEX IF NOT EXISTS idx_productos_categoria     ON productos(categoria);

CREATE TABLE IF NOT EXISTS carrito_items (
    id              BIGSERIAL     PRIMARY KEY,
    session_id      VARCHAR(100),
    usuario_id      BIGINT        REFERENCES usuarios(id) ON DELETE CASCADE,
    producto_id     BIGINT        NOT NULL REFERENCES productos(id),
    cantidad        INTEGER       NOT NULL CHECK (cantidad BETWEEN 1 AND 999),
    precio_unitario NUMERIC(10,2) NOT NULL CHECK (precio_unitario > 0),
    fecha_agregado  TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_carrito_session_producto UNIQUE (session_id, producto_id),
    CONSTRAINT uq_carrito_usuario_producto UNIQUE (usuario_id, producto_id),
    CONSTRAINT chk_carrito_owner CHECK (
        (session_id IS NOT NULL AND usuario_id IS NULL) OR
        (session_id IS NULL     AND usuario_id IS NOT NULL)
    )
);

CREATE INDEX IF NOT EXISTS idx_carrito_session ON carrito_items(session_id);
CREATE INDEX IF NOT EXISTS idx_carrito_usuario ON carrito_items(usuario_id);

CREATE TABLE IF NOT EXISTS pedidos (
    id                     BIGSERIAL          PRIMARY KEY,
    numero_pedido          VARCHAR(50)        NOT NULL UNIQUE,
    usuario_id             BIGINT             REFERENCES usuarios(id) ON DELETE SET NULL,
    estado                 estado_pedido_enum NOT NULL DEFAULT 'PENDIENTE',
    subtotal               NUMERIC(10,2)      CHECK (subtotal >= 0),
    igv                    NUMERIC(10,2)      CHECK (igv >= 0),
    costo_envio            NUMERIC(10,2)      CHECK (costo_envio >= 0),
    total                  NUMERIC(10,2)      NOT NULL CHECK (total > 0),
    direccion_envio        VARCHAR(200)       NOT NULL,
    ciudad_envio           VARCHAR(100),
    distrito_envio         VARCHAR(100),
    departamento_envio     VARCHAR(100),
    codigo_postal_envio    VARCHAR(10),
    telefono_contacto      VARCHAR(20),
    metodo_pago            metodo_pago_enum,
    estado_pago            estado_pago_enum   NOT NULL DEFAULT 'PENDIENTE',
    fecha_pago             TIMESTAMP,
    nombre_invitado        VARCHAR(100),
    email_invitado         VARCHAR(100),
    telefono_invitado      VARCHAR(20),
    notas                  TEXT,
    fecha_creacion         TIMESTAMP          NOT NULL DEFAULT NOW(),
    fecha_actualizacion    TIMESTAMP,
    fecha_entrega_estimada TIMESTAMP,
    fecha_entrega_real     TIMESTAMP,
    CONSTRAINT chk_pedido_cliente CHECK (
        (usuario_id IS NOT NULL) OR
        (email_invitado IS NOT NULL AND nombre_invitado IS NOT NULL)
    )
);

CREATE INDEX IF NOT EXISTS idx_pedidos_usuario        ON pedidos(usuario_id);
CREATE INDEX IF NOT EXISTS idx_pedidos_estado         ON pedidos(estado);
CREATE INDEX IF NOT EXISTS idx_pedidos_estado_pago    ON pedidos(estado_pago);
CREATE INDEX IF NOT EXISTS idx_pedidos_email_invitado ON pedidos(email_invitado);
CREATE INDEX IF NOT EXISTS idx_pedidos_fecha_creacion ON pedidos(fecha_creacion DESC);

CREATE TABLE IF NOT EXISTS detalle_pedidos (
    id              BIGSERIAL     PRIMARY KEY,
    pedido_id       BIGINT        NOT NULL REFERENCES pedidos(id)  ON DELETE CASCADE,
    producto_id     BIGINT        REFERENCES productos(id)         ON DELETE SET NULL,
    nombre_producto VARCHAR(200),
    marca_producto  VARCHAR(100),
    medida_producto VARCHAR(50),
    cantidad        INTEGER       NOT NULL CHECK (cantidad BETWEEN 1 AND 999),
    precio_unitario NUMERIC(10,2) NOT NULL CHECK (precio_unitario > 0),
    subtotal        NUMERIC(10,2) NOT NULL CHECK (subtotal > 0)
);

CREATE INDEX IF NOT EXISTS idx_detalle_pedido ON detalle_pedidos(pedido_id);

CREATE TABLE IF NOT EXISTS pagos (
    id                      BIGSERIAL        PRIMARY KEY,
    pedido_id               BIGINT           NOT NULL UNIQUE REFERENCES pedidos(id) ON DELETE CASCADE,
    metodo_pago             metodo_pago_enum NOT NULL,
    monto                   NUMERIC(10,2)    NOT NULL CHECK (monto > 0),
    moneda                  VARCHAR(3)       NOT NULL DEFAULT 'PEN',
    estado                  estado_pago_enum NOT NULL DEFAULT 'PENDIENTE',
    pasarela_pago           VARCHAR(50),
    transaction_id          VARCHAR(100)     UNIQUE,
    codigo_autorizacion     VARCHAR(100),
    ultimos_digitos_tarjeta VARCHAR(4),
    tipo_tarjeta            VARCHAR(20),
    notas                   TEXT,
    mensaje_error           TEXT,
    fecha_creacion          TIMESTAMP        NOT NULL DEFAULT NOW(),
    fecha_procesado         TIMESTAMP
);

CREATE TABLE IF NOT EXISTS articulos (
    id                  BIGSERIAL    PRIMARY KEY,
    titulo              VARCHAR(200) NOT NULL,
    extracto            VARCHAR(500),
    contenido           TEXT         NOT NULL,
    url_imagen          VARCHAR(500),
    publicado           BOOLEAN      NOT NULL DEFAULT FALSE,
    autor_id            BIGINT       REFERENCES usuarios(id) ON DELETE SET NULL,
    categoria           VARCHAR(100),
    tags                VARCHAR(500),
    fecha_creacion      TIMESTAMP    NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP,
    fecha_publicacion   TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_articulos_publicado ON articulos(publicado);
CREATE INDEX IF NOT EXISTS idx_articulos_categoria ON articulos(categoria);

CREATE TABLE IF NOT EXISTS idempotency_keys (
    key_value      VARCHAR(100) PRIMARY KEY,
    pedido_id      BIGINT       NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE,
    fecha_creacion TIMESTAMP    NOT NULL DEFAULT NOW()
);
