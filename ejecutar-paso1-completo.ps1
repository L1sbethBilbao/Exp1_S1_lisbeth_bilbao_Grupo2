# Paso 1 completo: Maven + Postman + Docker + Postman
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

function Stop-Port8080 {
    $prev = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    docker rm -f inscripcion-cursos 2>&1 | Out-Null
    $ErrorActionPreference = $prev
    $c = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($c) {
        Stop-Process -Id $c.OwningProcess -Force -ErrorAction SilentlyContinue
        Start-Sleep -Seconds 3
    }
}

Write-Host "`n===== PASO 1.1: mvn clean package =====" -ForegroundColor Yellow
.\mvnw.cmd clean package -q
if ($LASTEXITCODE -ne 0) { throw "Fallo mvn clean package" }

Write-Host "`n===== PASO 1.1: spring-boot:run =====" -ForegroundColor Yellow
Stop-Port8080

$env:ORACLE_JDBC_URL = "jdbc:oracle:thin:@miquintabd_high?TNS_ADMIN=C:/Users/lisbe/OneDrive/Escritorio/cloud_native_semana_1/inscripcion-cursos/Wallet_miQuintaBD"
$env:ORACLE_USERNAME = "INSCRIPCION_APP"
$env:ORACLE_PASSWORD = "InscripcionApp2026!"
$env:SERVER_PORT = "8080"

$mvnJob = Start-Process -FilePath ".\mvnw.cmd" -ArgumentList "spring-boot:run" -PassThru -NoNewWindow -WorkingDirectory $PSScriptRoot

$ready = $false
for ($i = 0; $i -lt 60; $i++) {
    Start-Sleep -Seconds 2
    try {
        $h = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -TimeoutSec 3
        if ($h.status -eq "UP") { $ready = $true; break }
    } catch {}
}
if (-not $ready) {
    Stop-Process -Id $mvnJob.Id -Force -ErrorAction SilentlyContinue
    throw "Maven no levanto en 8080"
}

Write-Host "`n===== PASO 1.1: Postman completo (Maven) =====" -ForegroundColor Yellow
& "$PSScriptRoot\verificar-postman-completo.ps1" -Modo "1.1 MAVEN"
if ($LASTEXITCODE -ne 0) {
    Stop-Process -Id $mvnJob.Id -Force -ErrorAction SilentlyContinue
    throw "Fallo Postman en Maven"
}

Stop-Process -Id $mvnJob.Id -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 4
Stop-Port8080

Write-Host "`n===== PASO 1.2: docker build =====" -ForegroundColor Yellow
docker build -t inscripcion-cursos:1.0 .
if ($LASTEXITCODE -ne 0) { throw "Fallo docker build" }

Write-Host "`n===== PASO 1.2: docker run =====" -ForegroundColor Yellow
& "$PSScriptRoot\docker-run-local.ps1"

$ready = $false
for ($i = 0; $i -lt 60; $i++) {
    Start-Sleep -Seconds 2
    try {
        $h = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -TimeoutSec 3
        if ($h.status -eq "UP") { $ready = $true; break }
    } catch {}
}
if (-not $ready) { throw "Docker no levanto en 8080" }

Write-Host "`n===== PASO 1.2: Postman completo (Docker) =====" -ForegroundColor Yellow
& "$PSScriptRoot\verificar-postman-completo.ps1" -Modo "1.2 DOCKER"
if ($LASTEXITCODE -ne 0) { throw "Fallo Postman en Docker" }

Write-Host "`n===== PASO 1 COMPLETO: OK =====" -ForegroundColor Green
