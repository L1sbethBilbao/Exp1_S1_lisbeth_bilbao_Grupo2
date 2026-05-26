@echo off
REM Paso 2: poblamiento de datos (ejecutar DESPUES de creacion-usuario-oracle.sql)
REM Edita ORACLE_PASSWORD si cambiaste la clave del usuario INSCRIPCION_APP

cd /d "%~dp0"

set ORACLE_JDBC_URL=jdbc:oracle:thin:@miquintabd_high?TNS_ADMIN=%~dp0Wallet_miQuintaBD
set ORACLE_USERNAME=INSCRIPCION_APP
set ORACLE_PASSWORD=InscripcionApp2026!
set SERVER_PORT=8080

call mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=init-db

pause
