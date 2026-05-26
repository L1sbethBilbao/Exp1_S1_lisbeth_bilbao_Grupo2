# API Inscripcion de Cursos

Microservicio Spring Boot para la plataforma educativa virtual (Semana 1 - Desarrollo Cloud Native).

Documentacion completa: **[DOCUMENTACION.md](DOCUMENTACION.md)** | Autoria: **[AUTOR.md](AUTOR.md)**

## Funcionalidades

| Metodo | Endpoint | Rol | Descripcion |
|--------|----------|-----|-------------|
| GET | `/api/cursos` | Publico | Lista cursos |
| POST | `/api/cursos` | ADMIN | Registra un nuevo curso |
| POST | `/api/inscripciones` | ESTUDIANTE | Inscribe estudiante con resumen y total |
| POST | `/api/admin/reset-pruebas` | ADMIN | Resetea datos de prueba (3 cursos, sin inscripciones) |
| GET | `/actuator/health` | Publico | Estado de la aplicacion |
| GET | `/swagger-ui.html` | Publico | Documentacion OpenAPI |

## Roles (Spring Security - HTTP Basic)

| Usuario | Password | Rol | Puede |
|---------|----------|-----|-------|
| `admin` | `Admin2026!` | ADMIN | Crear cursos, reset pruebas |
| `estudiante` | `Estudiante2026!` | ESTUDIANTE | Crear inscripciones |

Estos usuarios son de la **aplicacion**, no de Oracle. Oracle sigue usando `INSCRIPCION_APP`.

## Requisitos

- Java 21
- Maven 3.9+
- Oracle Cloud (Autonomous Database o instancia Oracle accesible)
- Variables de entorno configuradas

## Configuracion Oracle

Ejecutar en este orden en SQL Developer:

1. Conectar como **ADMIN** y ejecutar `scripts/creacion-usuario-oracle.sql`
2. Conectar como **INSCRIPCION_APP** y ejecutar `scripts/poblamiento-datos-oracle.sql`
3. Probar la API con Postman y luego ejecutar `scripts/consultas-verificacion-oracle.sql` para ver los mismos datos en Oracle

**Importante:** Postman guarda en el esquema `INSCRIPCION_APP`. El script de consultas usa `inscripcion_app.cursos` (etc.). Ejecuta primero el bloque **INICIO - VERIFICAR CONEXION** del script.

O usar `crear-tablas-oracle.bat` para el paso 2 (despues de crear el usuario).

Copia `.env.example` a `.env` y ajusta credenciales (no subas `.env` al repo).

Variables requeridas:

```bash
ORACLE_JDBC_URL=jdbc:oracle:thin:@miquintabd_high?TNS_ADMIN=...
ORACLE_USERNAME=INSCRIPCION_APP
ORACLE_PASSWORD=InscripcionApp2026!
SERVER_PORT=8080
```

## Ejecutar localmente

```bash
# Windows
set ORACLE_JDBC_URL=jdbc:oracle:thin:@...
set ORACLE_USERNAME=INSCRIPCION_APP
set ORACLE_PASSWORD=tu_password
.\mvnw.cmd spring-boot:run
```

## Compilar y probar

```bash
.\mvnw.cmd clean package
```

## Postman

Importa la coleccion desde `postman/inscripcion-cursos.postman_collection.json`.

**Flujo rapido (repetible):**

1. Carpeta **01 - ADMIN** en orden (auth ya configurada en la carpeta)
2. Carpeta **02 - ESTUDIANTE** en orden
3. Mismo orden en `scripts/consultas-verificacion-oracle.sql` (ADMIN 01-08, EST 01-09)

## Arquitectura

```
controller -> service (interface + impl) -> repository -> Oracle
                ^
              mapper (MapStruct Entity <-> DTO)
```

## Ejemplo respuesta inscripcion

```json
{
  "id": 1,
  "nombreEstudiante": "Maria Lopez",
  "emailEstudiante": "maria@duoc.cl",
  "fechaInscripcion": "2026-05-25T10:30:00",
  "cursos": [
    {
      "cursoId": 1,
      "nombreCurso": "Spring Boot Avanzado",
      "instructor": "Prof. Garcia",
      "duracionHoras": 40,
      "costoUnitario": 150000
    }
  ],
  "total": 150000
}
```

## Errores

Las respuestas de error usan formato uniforme:

```json
{
  "timestamp": "2026-05-25T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "No existe el curso con id: 99",
  "path": "/api/inscripciones"
}
```

## Proximos pasos (Semana 1)

- Dockerfile multi-stage
- Pipeline GitHub Actions (Docker Hub + EC2)
- Video demostrativo
