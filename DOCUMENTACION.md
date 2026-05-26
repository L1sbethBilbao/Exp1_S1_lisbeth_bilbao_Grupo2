# Documentacion del proyecto - Inscripcion de Cursos

**Asignatura:** Desarrollo Cloud Native - Semana 1  
**Autoría:** Ver [AUTOR.md](AUTOR.md) (completar tu nombre antes de entregar)

---

## 1. Descripcion general

Este proyecto es una **API REST** desarrollada con **Spring Boot** que permite:

- Consultar la lista de cursos disponibles (nombre, instructor, duracion, costo).
- Registrar nuevos cursos en la base de datos.
- Inscribir estudiantes en uno o mas cursos, con resumen de la inscripcion y total a pagar.

Todos los datos se **persisten en Oracle Cloud** (Autonomous Database). La aplicacion se conecta mediante JDBC con wallet de seguridad (`Wallet_miQuintaBD`).

---

## 2. Tecnologias utilizadas

| Tecnologia | Version / detalle | Uso |
|------------|-------------------|-----|
| **Java** | 21 | Lenguaje principal |
| **Spring Boot** | 4.0.6 | Framework backend |
| **Spring Web MVC** | - | REST API |
| **Spring Data JPA** | - | Persistencia con Hibernate |
| **Spring Security** | HTTP Basic | Roles ADMIN y ESTUDIANTE |
| **Spring Validation** | - | Validacion de DTOs |
| **MapStruct** | 1.6.3 | Mapeo Entity ↔ DTO |
| **Lombok** | - | Reduccion de boilerplate |
| **Springdoc OpenAPI** | 3.0.2 | Documentacion Swagger |
| **Spring Actuator** | - | Health check |
| **Oracle JDBC** | ojdbc11 + oraclepki | Conexion a Oracle Cloud |
| **Maven** | 3.9+ | Build y dependencias |
| **Oracle Cloud** | Autonomous DB 23.x | Base de datos en la nube |
| **Postman** | - | Pruebas de la API |

---

## 3. Conexion a Oracle Cloud

### 3.1 Como se conecta la aplicacion

La API **no usa H2 ni base local**. Se conecta a Oracle Cloud con:

- **Usuario de base de datos:** `INSCRIPCION_APP` (esquema donde viven las tablas).
- **Wallet:** carpeta `Wallet_miQuintaBD/` (certificados TLS; no subir al repositorio).
- **URL JDBC:** servicio `miquintabd_high` + ruta al wallet en `TNS_ADMIN`.

Variables de entorno (ver `.env.example`):

```bash
ORACLE_JDBC_URL=jdbc:oracle:thin:@miquintabd_high?TNS_ADMIN=C:/ruta/a/Wallet_miQuintaBD
ORACLE_USERNAME=INSCRIPCION_APP
ORACLE_PASSWORD=InscripcionApp2026!
SERVER_PORT=8080
```

### 3.2 Tablas en Oracle

| Tabla | Contenido |
|-------|-----------|
| `CURSOS` | Cursos ofrecidos (nombre unico) |
| `INSCRIPCIONES` | Cabecera de inscripcion (estudiante, total, fecha) |
| `INSCRIPCION_DETALLES` | Cursos incluidos en cada inscripcion |

Los datos que crea Postman quedan en el esquema **`INSCRIPCION_APP`**. En SQL Developer puedes verlos con:

```sql
SELECT * FROM inscripcion_app.cursos;
```

### 3.3 Scripts SQL incluidos

| Archivo | Proposito |
|---------|-----------|
| `scripts/creacion-usuario-oracle.sql` | Crear usuario `INSCRIPCION_APP` (ejecutar como ADMIN Oracle) |
| `scripts/poblamiento-datos-oracle.sql` | Crear tablas y 3 cursos de ejemplo |
| `scripts/consultas-verificacion-oracle.sql` | SELECT de verificacion alineados con Postman |

---

## 4. Como funciona el proyecto (arquitectura)

### 4.1 Capas

```
Cliente (Postman / Swagger)
        ↓
   Controller  (REST, validacion @Valid)
        ↓
   Service     (reglas de negocio, @Transactional)
        ↓
   Repository  (Spring Data JPA)
        ↓
   Oracle Cloud (INSCRIPCION_APP)
```

- **DTOs:** objetos de entrada/salida (request/response).
- **Mappers (MapStruct):** convierten Entity ↔ DTO.
- **GlobalExceptionHandler:** respuestas de error JSON uniformes (400, 404, 409, 500).

### 4.2 Endpoints principales

| Metodo | URL | Rol | Funcion |
|--------|-----|-----|---------|
| GET | `/api/cursos` | Publico | Listar cursos |
| POST | `/api/cursos` | ADMIN | Crear curso |
| POST | `/api/inscripciones` | ESTUDIANTE | Inscribir en uno o mas cursos |
| POST | `/api/admin/reset-pruebas` | ADMIN | Resetear datos de prueba |
| GET | `/actuator/health` | Publico | Estado de la app |
| GET | `/swagger-ui.html` | Publico | Documentacion interactiva |

### 4.3 Roles de la aplicacion

Son usuarios de **Spring Security** (no confundir con usuarios Oracle):

| Usuario | Password | Rol | Permisos |
|---------|----------|-----|----------|
| `admin` | `Admin2026!` | ADMIN | Crear cursos, reset de pruebas |
| `estudiante` | `Estudiante2026!` | ESTUDIANTE | Crear inscripciones |

Autenticacion: **HTTP Basic** en el header `Authorization`.

### 4.4 Reglas de negocio

- Nombre de curso **unico** (409 si se repite).
- Inscripcion con **al menos un curso** (maximo 10).
- **Total** = suma de costos de los cursos seleccionados.
- IDs de curso **sin duplicados** en la misma inscripcion.
- Validacion de campos con Bean Validation (400 si datos invalidos).

---

## 5. Coleccion Postman

### 5.1 Como importar

1. Abrir **Postman**.
2. **Import** → seleccionar archivo:
   ```
   postman/inscripcion-cursos.postman_collection.json
   ```
3. La variable `baseUrl` debe ser `http://localhost:8080` (o la IP de EC2 en despliegue).

### 5.2 Para que sirve Postman

Postman permite **probar todos los endpoints** sin escribir codigo:

- Verifica que la API responde correctamente.
- Demuestra el flujo completo para la evaluacion y el video.
- Confirma que los datos llegan a **Oracle Cloud** (cruzando con el script SQL).

### 5.3 Estructura de la coleccion

| Carpeta | Auth | Contenido |
|---------|------|-----------|
| **01 - ADMIN** | `admin` / `Admin2026!` | Reset, crear cursos, listar, pruebas de error 400/409 |
| **02 - ESTUDIANTE** | `estudiante` / `Estudiante2026!` | Inscripciones exitosas y pruebas de error |
| **03 - Infraestructura** | Sin auth | Health check y OpenAPI |

### 5.4 Flujo recomendado de prueba

1. Levantar la aplicacion (`mvnw spring-boot:run` con variables Oracle).
2. **INFRA 01** → comprobar `status: UP`.
3. Carpeta **01 - ADMIN** del **ADMIN 01** al **ADMIN 08** en orden.
4. Carpeta **02 - ESTUDIANTE** del **EST 01** al **EST 09** en orden.
5. Tras cada request, ejecutar el bloque equivalente en `scripts/consultas-verificacion-oracle.sql`.

**ADMIN 01 (Reset)** limpia la base y deja 3 cursos (ids 1, 2, 3) para repetir pruebas sin ejecutar SQL manualmente.

---

## 6. Como ejecutar el proyecto

### 6.1 Requisitos previos

- Java 21 instalado.
- Wallet Oracle en `Wallet_miQuintaBD/`.
- Usuario `INSCRIPCION_APP` creado y tablas pobladas (scripts en `scripts/`).

### 6.2 Compilar

```powershell
cd inscripcion-cursos
.\mvnw.cmd clean package
```

### 6.3 Ejecutar (Windows PowerShell)

```powershell
$env:ORACLE_JDBC_URL="jdbc:oracle:thin:@miquintabd_high?TNS_ADMIN=C:/ruta/completa/Wallet_miQuintaBD"
$env:ORACLE_USERNAME="INSCRIPCION_APP"
$env:ORACLE_PASSWORD="InscripcionApp2026!"
$env:SERVER_PORT="8080"
.\mvnw.cmd spring-boot:run
```

### 6.4 Verificar

- Navegador o Postman: `http://localhost:8080/actuator/health` → `{"status":"UP"}`
- Swagger: `http://localhost:8080/swagger-ui.html`

---

## 7. Estructura del repositorio

```
inscripcion-cursos/
├── src/main/java/          Codigo fuente (controllers, services, entities...)
├── src/main/resources/     application.yml, scripts internos
├── src/test/java/          Tests unitarios
├── scripts/                SQL Oracle (creacion, poblamiento, consultas)
├── postman/                Coleccion Postman importable
├── Wallet_miQuintaBD/      Wallet Oracle (local, no commitear)
├── pom.xml                 Dependencias Maven
├── README.md               Guia rapida
├── DOCUMENTACION.md        Este documento
└── .env.example            Plantilla de variables de entorno
```

---

## 8. Verificacion Postman + Oracle

Flujo de demostracion para el docente:

1. Ejecutar request en **Postman** (ej. ADMIN 03 crear curso).
2. Abrir **SQL Developer** conectado a Oracle Cloud.
3. Ejecutar bloque **ADMIN 03** en `consultas-verificacion-oracle.sql`.
4. Confirmar que la fila existe en `inscripcion_app.cursos`.

Asi se demuestra que la API **lee y escribe en Oracle Cloud**, no en memoria.

---

## 9. Proximos pasos (Semana 1)

- [ ] Dockerfile multi-stage
- [ ] Pipeline GitHub Actions (build → Docker Hub → deploy EC2)
- [ ] Video demostrativo con Postman + Oracle + despliegue

---

## 10. Entregables

- Repositorio GitHub con el código fuente
- Zip con `DOCUMENTACION.md` y capturas de Postman / SQL Developer
- Video demostrativo del flujo completo
