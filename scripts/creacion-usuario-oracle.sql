-- =============================================================================
-- CREACION DE USUARIO
-- =============================================================================
-- Conexion: ADMIN (SQL Developer + wallet miquintabd_high)
-- Ejecutar ANTES de poblamiento-datos-oracle.sql
--
-- IMPORTANTE:
-- - Cambia la contraseña antes de usar en produccion.
-- - Debe tener mayuscula, numero y simbolo.
-- - No subas contraseñas reales al repositorio.
-- =============================================================================

-- Opcional (solo desarrollo): descomenta si necesitas recrear el usuario
-- DROP USER inscripcion_app CASCADE;

CREATE USER inscripcion_app IDENTIFIED BY "InscripcionApp2026!";

GRANT CREATE SESSION TO inscripcion_app;
GRANT CREATE TABLE TO inscripcion_app;
GRANT CREATE SEQUENCE TO inscripcion_app;
GRANT CREATE VIEW TO inscripcion_app;
GRANT CREATE TRIGGER TO inscripcion_app;

ALTER USER inscripcion_app QUOTA UNLIMITED ON DATA;

COMMIT;

-- =============================================================================
-- SIGUIENTE PASO:
-- Crear conexion INSCRIPCION_APP en SQL Developer y ejecutar:
--   scripts/poblamiento-datos-oracle.sql
-- =============================================================================
