-- =============================================================================
-- CONSULTAS DE VERIFICACION EN ORACLE
-- =============================================================================
-- Mismo orden que postman/inscripcion-cursos.postman_collection.json
--
--   Carpeta 01 - ADMIN      -> bloques ADMIN 01 a ADMIN 08
--   Carpeta 02 - ESTUDIANTE -> bloques EST 01 a EST 09
--   Carpeta 03 - Infra      -> sin consulta Oracle
--
-- Ejecutar cada bloque JUSTO DESPUES del request Postman equivalente.
-- Usa inscripcion_app.* para ver los mismos datos que la API.
-- =============================================================================


-- =============================================================================
-- INICIO - VERIFICAR CONEXION (una sola vez al abrir SQL Developer)
-- =============================================================================

SELECT USER AS usuario_sql_developer FROM DUAL;

SELECT COUNT(*) AS total_cursos_app
FROM inscripcion_app.cursos;


-- =============================================================================
-- CARPETA 01 - ADMIN
-- Auth Postman: admin / Admin2026!
-- =============================================================================

-- -----------------------------------------------------------------------------
-- ADMIN 01 - Reset datos prueba
-- POST /api/admin/reset-pruebas (Borra datos, reinicia secuencias y carga los 3 cursos.)
-- -----------------------------------------------------------------------------

SELECT COUNT(*) AS total_cursos_despues_reset
FROM inscripcion_app.cursos;

SELECT COUNT(*) AS total_inscripciones_despues_reset
FROM inscripcion_app.inscripciones;

SELECT id, nombre, instructor, duracion_horas, costo
FROM inscripcion_app.cursos
ORDER BY id;

-- -----------------------------------------------------------------------------
-- ADMIN 02 - Listar cursos
-- GET /api/cursos
-- -----------------------------------------------------------------------------

SELECT id, nombre, instructor, duracion_horas, costo
FROM inscripcion_app.cursos
ORDER BY id;

-- -----------------------------------------------------------------------------
-- ADMIN 03 - Crear curso Java Cloud
-- POST /api/cursos
-- -----------------------------------------------------------------------------

SELECT id, nombre, instructor, duracion_horas, costo
FROM inscripcion_app.cursos
WHERE nombre = 'Java Cloud Native';

-- -----------------------------------------------------------------------------
-- ADMIN 04 - Crear curso Microservicios
-- POST /api/cursos
-- -----------------------------------------------------------------------------

SELECT id, nombre, instructor, duracion_horas, costo
FROM inscripcion_app.cursos
WHERE nombre = 'Microservicios con Spring';

-- -----------------------------------------------------------------------------
-- ADMIN 05 - Listar cursos (despues de crear)
-- GET /api/cursos
-- -----------------------------------------------------------------------------

SELECT COUNT(*) AS total_cursos FROM inscripcion_app.cursos;

SELECT id, nombre, instructor, duracion_horas, costo
FROM inscripcion_app.cursos
ORDER BY id;

-- -----------------------------------------------------------------------------
-- ADMIN 06 - Crear curso nombre vacio (400)
-- Postman debe fallar; Oracle NO debe guardar curso invalido
-- -----------------------------------------------------------------------------

SELECT COUNT(*) AS cursos_invalidos
FROM inscripcion_app.cursos
WHERE instructor = 'Prof. Test'
  AND (nombre IS NULL OR TRIM(nombre) = '');

-- -----------------------------------------------------------------------------
-- ADMIN 07 - Crear curso costo negativo (400)
-- -----------------------------------------------------------------------------

SELECT COUNT(*) AS curso_no_debe_existir
FROM inscripcion_app.cursos
WHERE nombre = 'Curso Invalido';

-- -----------------------------------------------------------------------------
-- ADMIN 08 - Crear curso duplicado (409)
-- Debe existir UNA sola fila Spring Boot Avanzado (Eso es correcto: el nombre es único y la app rechazó el duplicado.)
-- -----------------------------------------------------------------------------

SELECT nombre, COUNT(*) AS cantidad
FROM inscripcion_app.cursos
WHERE nombre = 'Spring Boot Avanzado'
GROUP BY nombre;


-- =============================================================================
-- CARPETA 02 - ESTUDIANTE
-- Auth Postman: estudiante / Estudiante2026!
-- =============================================================================

-- -----------------------------------------------------------------------------
-- EST 01 - Listar cursos
-- GET /api/cursos
-- -----------------------------------------------------------------------------

SELECT id, nombre, instructor, duracion_horas, costo
FROM inscripcion_app.cursos
ORDER BY id;

-- -----------------------------------------------------------------------------
-- EST 02 - Inscripcion 1 curso (Pedro Ramirez, cursoIds: [1])
-- POST /api/inscripciones
-- 2 SELECT: cabecera + detalle (NO insertan datos)
-- -----------------------------------------------------------------------------

SELECT i.id, i.nombre_estudiante, i.email_estudiante, i.total, i.fecha_inscripcion
FROM inscripcion_app.inscripciones i
WHERE i.nombre_estudiante = 'Pedro Ramirez'
ORDER BY i.id;

SELECT i.id AS inscripcion_id, i.nombre_estudiante, c.id AS curso_id,
       c.nombre AS nombre_curso, d.costo_unitario
FROM inscripcion_app.inscripciones i
JOIN inscripcion_app.inscripcion_detalles d ON d.inscripcion_id = i.id
JOIN inscripcion_app.cursos c ON c.id = d.curso_id
WHERE i.nombre_estudiante = 'Pedro Ramirez'
ORDER BY i.id, c.id;

-- -----------------------------------------------------------------------------
-- EST 03 - Inscripcion varios cursos (Maria Lopez, cursoIds: [1,2,3])
-- POST /api/inscripciones
-- -----------------------------------------------------------------------------

SELECT i.id, i.nombre_estudiante, i.email_estudiante, i.total, i.fecha_inscripcion
FROM inscripcion_app.inscripciones i
WHERE i.nombre_estudiante = 'Maria Lopez';

SELECT i.nombre_estudiante, c.id AS curso_id, c.nombre AS nombre_curso,
       d.costo_unitario, i.total AS total_inscripcion
FROM inscripcion_app.inscripciones i
JOIN inscripcion_app.inscripcion_detalles d ON d.inscripcion_id = i.id
JOIN inscripcion_app.cursos c ON c.id = d.curso_id
WHERE i.nombre_estudiante = 'Maria Lopez'
ORDER BY c.id;

SELECT i.nombre_estudiante, i.total AS total_guardado,
       SUM(d.costo_unitario) AS total_calculado,
       CASE WHEN i.total = SUM(d.costo_unitario) THEN 'VALIDO' ELSE 'ERROR' END AS estado_total
FROM inscripcion_app.inscripciones i
JOIN inscripcion_app.inscripcion_detalles d ON d.inscripcion_id = i.id
WHERE i.nombre_estudiante = 'Maria Lopez'
GROUP BY i.nombre_estudiante, i.total;

-- -----------------------------------------------------------------------------
-- EST 04 - Inscripcion sin email (Ana Contreras, cursoIds: [2])
-- POST /api/inscripciones
-- -----------------------------------------------------------------------------

SELECT i.id, i.nombre_estudiante, i.email_estudiante, i.total, c.nombre AS curso_inscrito
FROM inscripcion_app.inscripciones i
JOIN inscripcion_app.inscripcion_detalles d ON d.inscripcion_id = i.id
JOIN inscripcion_app.cursos c ON c.id = d.curso_id
WHERE i.nombre_estudiante = 'Ana Contreras';

-- -----------------------------------------------------------------------------
-- EST 05 - Inscripcion sin cursos (400)
-- NO debe crear inscripcion valida para Juan Perez
-- -----------------------------------------------------------------------------

SELECT COUNT(*) AS inscripciones_juan_perez
FROM inscripcion_app.inscripciones
WHERE nombre_estudiante = 'Juan Perez';

-- -----------------------------------------------------------------------------
-- EST 06 - Inscripcion curso inexistente (404)
-- -----------------------------------------------------------------------------

SELECT COUNT(*) AS detalle_curso_9999
FROM inscripcion_app.inscripcion_detalles
WHERE curso_id = 9999;

-- -----------------------------------------------------------------------------
-- EST 07 - Inscripcion cursos duplicados (400)
-- -----------------------------------------------------------------------------

SELECT COUNT(*) AS inscripciones_juan_perez
FROM inscripcion_app.inscripciones
WHERE nombre_estudiante = 'Juan Perez';

-- -----------------------------------------------------------------------------
-- EST 08 - Inscripcion email invalido (400)
-- -----------------------------------------------------------------------------

SELECT COUNT(*) AS emails_invalidos
FROM inscripcion_app.inscripciones
WHERE email_estudiante = 'correo-invalido';

-- -----------------------------------------------------------------------------
-- EST 09 - Inscripcion sin nombre (400)
-- -----------------------------------------------------------------------------

SELECT COUNT(*) AS nombres_vacios
FROM inscripcion_app.inscripciones
WHERE nombre_estudiante IS NULL
   OR TRIM(nombre_estudiante) = '';


-- =============================================================================
-- CARPETA 03 - INFRAESTRUCTURA
-- INFRA 01 Health (sirve: ver si Spring Boot está corriendo.) 
-- INFRA 02 OpenAPI -> no aplica consulta Oracle (sirve: obtener la documentación de la API en JSON (Swagger/OpenAPI).)
-- =============================================================================


-- =============================================================================
-- RESUMEN FINAL (al terminar ADMIN + ESTUDIANTE)
-- =============================================================================

SELECT 'CURSOS' AS tabla, COUNT(*) AS total FROM inscripcion_app.cursos
UNION ALL
SELECT 'INSCRIPCIONES', COUNT(*) FROM inscripcion_app.inscripciones
UNION ALL
SELECT 'INSCRIPCION_DETALLES', COUNT(*) FROM inscripcion_app.inscripcion_detalles;

SELECT i.id AS inscripcion_id, i.nombre_estudiante, i.total AS total_guardado,
       NVL(SUM(d.costo_unitario), 0) AS total_calculado,
       CASE WHEN i.total = NVL(SUM(d.costo_unitario), 0) THEN 'VALIDO'
            ELSE 'ERROR - NO COINCIDE' END AS estado_total
FROM inscripcion_app.inscripciones i
LEFT JOIN inscripcion_app.inscripcion_detalles d ON d.inscripcion_id = i.id
GROUP BY i.id, i.nombre_estudiante, i.total
ORDER BY i.id;
