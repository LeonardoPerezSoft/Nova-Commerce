#!/usr/bin/env pwsh

# Script para gestionar servicios de Nova Commerce con Docker en Windows

# Configurar encoding
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

# Colores
$RedColor = "`e[91m"
$GreenColor = "`e[92m"
$YellowColor = "`e[93m"
$BlueColor = "`e[94m"
$ResetColor = "`e[0m"

function Print-Header {
    param([string]$message)
    Write-Host "$BlueColor========================================$ResetColor"
    Write-Host "$BlueColor$message$ResetColor"
    Write-Host "$BlueColor========================================$ResetColor"
}

function Print-Success {
    param([string]$message)
    Write-Host "$GreenColor✓ $message$ResetColor"
}

function Print-Error {
    param([string]$message)
    Write-Host "$RedColor✗ $message$ResetColor"
}

function Print-Info {
    param([string]$message)
    Write-Host "$YellowColor→ $message$ResetColor"
}

function Show-Menu {
    Write-Host ""
    Write-Host "Opciones disponibles:"
    Write-Host "1. Build - Construir todas las imágenes"
    Write-Host "2. Build Single - Construir una imagen específica"
    Write-Host "3. Up - Iniciar todos los servicios"
    Write-Host "4. Down - Detener todos los servicios"
    Write-Host "5. Restart - Reiniciar todos los servicios"
    Write-Host "6. Logs - Ver logs de los servicios"
    Write-Host "7. Status - Ver estado de los servicios"
    Write-Host "8. Clean - Limpiar y reiniciar desde cero"
    Write-Host "9. Test Health - Probar salud de los servicios"
    Write-Host "0. Salir"
    Write-Host ""
}

function Build-All {
    Print-Header "Construyendo todas las imágenes Docker"
    docker-compose build
    if ($LASTEXITCODE -eq 0) {
        Print-Success "Todas las imágenes se construyeron exitosamente"
    } else {
        Print-Error "Error durante la construcción"
    }
}

function Build-Single {
    Write-Host "Servicios disponibles:"
    Write-Host "1. auth-service"
    Write-Host "2. user-service"
    Write-Host "3. customer-service"
    Write-Host "4. product-service"
    Write-Host "5. order-service"
    Write-Host "6. nova-gateway"
    [int]$serviceChoice = Read-Host "Selecciona el servicio a construir"
    
    $service = switch ($serviceChoice) {
        1 { "auth-service" }
        2 { "user-service" }
        3 { "customer-service" }
        4 { "product-service" }
        5 { "order-service" }
        6 { "nova-gateway" }
        default { $null }
    }
    
    if ($service) {
        Print-Header "Construyendo $service"
        docker-compose build $service
        if ($LASTEXITCODE -eq 0) {
            Print-Success "$service se construyó exitosamente"
        }
    } else {
        Print-Error "Opción inválida"
    }
}

function Up-Services {
    Print-Header "Iniciando servicios"
    docker-compose up -d
    if ($LASTEXITCODE -eq 0) {
        Print-Success "Servicios iniciados"
        Print-Info "Esperando a que los servicios estén listos..."
        Start-Sleep -Seconds 5
        Status-Services
    }
}

function Down-Services {
    Print-Header "Deteniendo servicios"
    docker-compose down
    if ($LASTEXITCODE -eq 0) {
        Print-Success "Servicios detenidos"
    }
}

function Restart-Services {
    Print-Header "Reiniciando servicios"
    docker-compose restart
    if ($LASTEXITCODE -eq 0) {
        Print-Success "Servicios reiniciados"
    }
}

function View-Logs {
    Write-Host "Opciones:"
    Write-Host "1. Ver logs de todos los servicios"
    Write-Host "2. Ver logs de un servicio específico"
    [int]$logChoice = Read-Host "Selecciona una opción"
    
    switch ($logChoice) {
        1 {
            docker-compose logs -f --tail=50
        }
        2 {
            Write-Host "Servicios disponibles:"
            Write-Host "1. auth-service"
            Write-Host "2. user-service"
            Write-Host "3. customer-service"
            Write-Host "4. product-service"
            Write-Host "5. order-service"
            Write-Host "6. nova-gateway"
            Write-Host "7. postgres"
            [int]$serviceChoice = Read-Host "Selecciona el servicio"
            
            $service = switch ($serviceChoice) {
                1 { "auth-service" }
                2 { "user-service" }
                3 { "customer-service" }
                4 { "product-service" }
                5 { "order-service" }
                6 { "nova-gateway" }
                7 { "postgres" }
                default { $null }
            }
            
            if ($service) {
                docker-compose logs -f --tail=50 $service
            } else {
                Print-Error "Opción inválida"
            }
        }
        default {
            Print-Error "Opción inválida"
        }
    }
}

function Status-Services {
    Print-Header "Estado de servicios"
    docker-compose ps
}

function Clean-And-Restart {
    Print-Header "Limpiando y reiniciando desde cero"
    Print-Info "Esto eliminará todos los contenedores y volúmenes"
    $confirm = Read-Host "¿Está seguro? (s/n)"
    
    if ($confirm -eq "s" -or $confirm -eq "S") {
        docker-compose down -v
        Print-Success "Contenedores y volúmenes eliminados"
        
        Print-Info "Construyendo imágenes..."
        docker-compose build --no-cache
        
        Print-Info "Iniciando servicios..."
        docker-compose up -d
        
        Print-Success "Sistema reiniciado exitosamente"
        Start-Sleep -Seconds 5
        Status-Services
    } else {
        Print-Info "Operación cancelada"
    }
}

function Test-Health {
    Print-Header "Probando salud de los servicios"
    
    $services = @(
        @{"name" = "auth-service"; "port" = 8080},
        @{"name" = "user-service"; "port" = 8081},
        @{"name" = "customer-service"; "port" = 8082},
        @{"name" = "product-service"; "port" = 8083},
        @{"name" = "order-service"; "port" = 8085},
        @{"name" = "nova-gateway"; "port" = 8090}
    )
    
    foreach ($service in $services) {
        Write-Host -NoNewline "Probando $($service.name) ($($service.port))... "
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:$($service.port)/actuator/health" -UseBasicParsing -TimeoutSec 5 -ErrorAction SilentlyContinue
            if ($response.StatusCode -eq 200) {
                Print-Success "$($service.name) está operacional"
            } else {
                Print-Error "$($service.name) no responde"
            }
        } catch {
            Print-Error "$($service.name) no responde"
        }
    }
    
    Write-Host ""
    Write-Host -NoNewline "Probando PostgreSQL... "
    try {
        $result = docker-compose exec -T postgres pg_isready -U postgres 2>&1
        if ($LASTEXITCODE -eq 0) {
            Print-Success "PostgreSQL está operacional"
        } else {
            Print-Error "PostgreSQL no responde"
        }
    } catch {
        Print-Error "PostgreSQL no responde"
    }
}

function Main {
    Print-Header "Nova Commerce - Docker Management Tool"
    
    while ($true) {
        Show-Menu
        [int]$choice = Read-Host "Selecciona una opción"
        
        switch ($choice) {
            1 { Build-All }
            2 { Build-Single }
            3 { Up-Services }
            4 { Down-Services }
            5 { Restart-Services }
            6 { View-Logs }
            7 { Status-Services }
            8 { Clean-And-Restart }
            9 { Test-Health }
            0 { Print-Info "Saliendo..."; exit }
            default { Print-Error "Opción inválida" }
        }
    }
}

# Ejecutar main
Main
