-- ============================================================
-- Datos iniciales del sistema
-- Se ejecuta después de schema.sql en cada arranque.
-- Todos los INSERT usan ON CONFLICT para ser idempotentes.
-- ============================================================

-- ===== ROLES =====
INSERT INTO roles (nombre, descripcion) VALUES
    ('ADMIN',    'Administrador del sistema con acceso completo'),
    ('VENDEDOR', 'Vendedor con acceso a gestión de pedidos'),
    ('USER',     'Cliente registrado')
ON CONFLICT (nombre) DO NOTHING;

-- ===== USUARIO ADMINISTRADOR INICIAL =====
-- IMPORTANTE: cambiar la contraseña en producción.
-- Hash BCrypt de 'Admin2026!' generado con strength 10.
-- Para regenerar: use BCryptPasswordEncoder().encode("nueva_pass")
INSERT INTO usuarios (username, email, password, nombre_completo, activo, fecha_creacion, fecha_actualizacion)
VALUES (
    'admin@haidainversiones.com',
    'admin@haidainversiones.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lHuu',
    'Administrador del Sistema',
    TRUE,
    NOW(),
    NOW()
) ON CONFLICT (email) DO NOTHING;

-- Asignar rol ADMIN al administrador
INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id
FROM usuarios u, roles r
WHERE u.email = 'admin@haidainversiones.com'
  AND r.nombre = 'ADMIN'
ON CONFLICT DO NOTHING;
