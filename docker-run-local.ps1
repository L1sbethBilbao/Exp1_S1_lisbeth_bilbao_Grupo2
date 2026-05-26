# Paso 1.2 - Levantar la app en Docker local (puerto 8080)
# Ejecutar desde la carpeta inscripcion-cursos

$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

$walletPath = Join-Path $PSScriptRoot "Wallet_miQuintaBD"
if (-not (Test-Path $walletPath)) {
    Write-Error "No se encontro Wallet_miQuintaBD. Coloca el wallet Oracle en esta carpeta."
}

$prevErr = $ErrorActionPreference
$ErrorActionPreference = "Continue"
docker rm -f inscripcion-cursos 2>&1 | Out-Null
$ErrorActionPreference = $prevErr

docker run -d `
  -p 8080:8080 `
  --name inscripcion-cursos `
  -e ORACLE_JDBC_URL="jdbc:oracle:thin:@miquintabd_high?TNS_ADMIN=/wallet" `
  -e ORACLE_USERNAME="INSCRIPCION_APP" `
  -e ORACLE_PASSWORD="InscripcionApp2026!" `
  -e SERVER_PORT="8080" `
  -v "${walletPath}:/wallet" `
  inscripcion-cursos:1.0

Write-Host "Contenedor iniciado. Probar: http://localhost:8080/actuator/health"
