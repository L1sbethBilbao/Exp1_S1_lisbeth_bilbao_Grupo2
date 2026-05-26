param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$Modo = "MAVEN"
)

$adminCred = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("admin:Admin2026!"))
$estCred = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("estudiante:Estudiante2026!"))

$passed = 0
$failed = 0
$results = @()

function Invoke-ApiTest {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        [int]$ExpectedStatus,
        [string]$AuthHeader = $null,
        [string]$Body = $null
    )

    $headers = @{}
    if ($AuthHeader) { $headers["Authorization"] = "Basic $AuthHeader" }
    if ($Body) { $headers["Content-Type"] = "application/json" }

    try {
        $params = @{
            Uri = $Url
            Method = $Method
            Headers = $headers
            UseBasicParsing = $true
        }
        if ($Body) { $params["Body"] = $Body }

        $response = Invoke-WebRequest @params
        $status = [int]$response.StatusCode
    } catch {
        if ($_.Exception.Response) {
            $status = [int]$_.Exception.Response.StatusCode
        } else {
            $script:failed++
            $script:results += [PSCustomObject]@{ Modo = $Modo; Request = $Name; Expected = $ExpectedStatus; Actual = "ERROR"; Ok = "NO" }
            return
        }
    }

    $ok = ($status -eq $ExpectedStatus)
    if ($ok) { $script:passed++ } else { $script:failed++ }
    $script:results += [PSCustomObject]@{
        Modo = $Modo
        Request = $Name
        Expected = $ExpectedStatus
        Actual = $status
        Ok = if ($ok) { "SI" } else { "NO" }
    }
}

Write-Host "`n========== POSTMAN COMPLETO ($Modo) ==========" -ForegroundColor Cyan

Invoke-ApiTest "INFRA 01 Health" "GET" "$BaseUrl/actuator/health" 200
Invoke-ApiTest "ADMIN 01 Reset" "POST" "$BaseUrl/api/admin/reset-pruebas" 200 $adminCred
Invoke-ApiTest "ADMIN 02 Listar cursos" "GET" "$BaseUrl/api/cursos" 200
Invoke-ApiTest "ADMIN 03 Java Cloud" "POST" "$BaseUrl/api/cursos" 201 $adminCred '{"nombre":"Java Cloud Native","instructor":"Prof. Silva","duracionHoras":45,"costo":175000}'
Invoke-ApiTest "ADMIN 04 Microservicios" "POST" "$BaseUrl/api/cursos" 201 $adminCred '{"nombre":"Microservicios con Spring","instructor":"Prof. Torres","duracionHoras":50,"costo":200000}'
Invoke-ApiTest "ADMIN 05 Listar (5 cursos)" "GET" "$BaseUrl/api/cursos" 200
Invoke-ApiTest "ADMIN 06 Nombre vacio" "POST" "$BaseUrl/api/cursos" 400 $adminCred '{"nombre":"","instructor":"Prof. Test","duracionHoras":20,"costo":50000}'
Invoke-ApiTest "ADMIN 07 Costo negativo" "POST" "$BaseUrl/api/cursos" 400 $adminCred '{"nombre":"Curso Invalido","instructor":"Prof. Test","duracionHoras":20,"costo":-100}'
Invoke-ApiTest "ADMIN 08 Duplicado" "POST" "$BaseUrl/api/cursos" 409 $adminCred '{"nombre":"Spring Boot Avanzado","instructor":"Prof. Garcia","duracionHoras":40,"costo":150000}'

Invoke-ApiTest "EST 01 Listar cursos" "GET" "$BaseUrl/api/cursos" 200
Invoke-ApiTest "EST 02 Pedro 1 curso" "POST" "$BaseUrl/api/inscripciones" 201 $estCred '{"nombreEstudiante":"Pedro Ramirez","emailEstudiante":"pedro@duoc.cl","cursoIds":[1]}'
Invoke-ApiTest "EST 03 Maria varios" "POST" "$BaseUrl/api/inscripciones" 201 $estCred '{"nombreEstudiante":"Maria Lopez","emailEstudiante":"maria@duoc.cl","cursoIds":[1,2,3]}'
Invoke-ApiTest "EST 04 Ana sin email" "POST" "$BaseUrl/api/inscripciones" 201 $estCred '{"nombreEstudiante":"Ana Contreras","cursoIds":[2]}'
Invoke-ApiTest "EST 05 Sin cursos" "POST" "$BaseUrl/api/inscripciones" 400 $estCred '{"nombreEstudiante":"Juan Perez","emailEstudiante":"juan@duoc.cl","cursoIds":[]}'
Invoke-ApiTest "EST 06 Curso 9999" "POST" "$BaseUrl/api/inscripciones" 404 $estCred '{"nombreEstudiante":"Juan Perez","emailEstudiante":"juan@duoc.cl","cursoIds":[9999]}'
Invoke-ApiTest "EST 07 IDs duplicados" "POST" "$BaseUrl/api/inscripciones" 400 $estCred '{"nombreEstudiante":"Juan Perez","emailEstudiante":"juan@duoc.cl","cursoIds":[1,1]}'
Invoke-ApiTest "EST 08 Email invalido" "POST" "$BaseUrl/api/inscripciones" 400 $estCred '{"nombreEstudiante":"Juan Perez","emailEstudiante":"correo-invalido","cursoIds":[1]}'
Invoke-ApiTest "EST 09 Sin nombre" "POST" "$BaseUrl/api/inscripciones" 400 $estCred '{"nombreEstudiante":"","cursoIds":[1]}'

Invoke-ApiTest "INFRA 02 OpenAPI" "GET" "$BaseUrl/api-docs" 200

$results | Format-Table -AutoSize
Write-Host "PASARON: $passed | FALLARON: $failed" -ForegroundColor $(if ($failed -eq 0) { "Green" } else { "Red" })
if ($failed -gt 0) { exit 1 }
