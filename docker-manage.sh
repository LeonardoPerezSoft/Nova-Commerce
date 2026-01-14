#!/bin/bash

# Script para gestionar servicios de Nova Commerce con Docker

set -e

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Funciones
print_header() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}→ $1${NC}"
}

# Menú principal
show_menu() {
    echo ""
    echo "Opciones disponibles:"
    echo "1. Build - Construir todas las imágenes"
    echo "2. Build Single - Construir una imagen específica"
    echo "3. Up - Iniciar todos los servicios"
    echo "4. Down - Detener todos los servicios"
    echo "5. Restart - Reiniciar todos los servicios"
    echo "6. Logs - Ver logs de los servicios"
    echo "7. Status - Ver estado de los servicios"
    echo "8. Clean - Limpiar y reiniciar desde cero"
    echo "9. Test Health - Probar salud de los servicios"
    echo "0. Salir"
    echo ""
    read -p "Selecciona una opción: " choice
}

# Build todas las imágenes
build_all() {
    print_header "Construyendo todas las imágenes Docker"
    docker-compose build
    print_success "Todas las imágenes se construyeron exitosamente"
}

# Build una imagen específica
build_single() {
    echo "Servicios disponibles:"
    echo "1. auth-service"
    echo "2. user-service"
    echo "3. customer-service"
    echo "4. product-service"
    echo "5. order-service"
    echo "6. nova-gateway"
    read -p "Selecciona el servicio a construir: " service_choice
    
    case $service_choice in
        1) SERVICE="auth-service" ;;
        2) SERVICE="user-service" ;;
        3) SERVICE="customer-service" ;;
        4) SERVICE="product-service" ;;
        5) SERVICE="order-service" ;;
        6) SERVICE="nova-gateway" ;;
        *) print_error "Opción inválida"; return ;;
    esac
    
    print_header "Construyendo $SERVICE"
    docker-compose build $SERVICE
    print_success "$SERVICE se construyó exitosamente"
}

# Iniciar servicios
up_services() {
    print_header "Iniciando servicios"
    docker-compose up -d
    print_success "Servicios iniciados"
    
    print_info "Esperando a que los servicios estén listos..."
    sleep 5
    
    echo ""
    status_services
}

# Detener servicios
down_services() {
    print_header "Deteniendo servicios"
    docker-compose down
    print_success "Servicios detenidos"
}

# Reiniciar servicios
restart_services() {
    print_header "Reiniciando servicios"
    docker-compose restart
    print_success "Servicios reiniciados"
}

# Ver logs
view_logs() {
    echo "Opciones:"
    echo "1. Ver logs de todos los servicios"
    echo "2. Ver logs de un servicio específico"
    read -p "Selecciona una opción: " log_choice
    
    case $log_choice in
        1)
            docker-compose logs -f --tail=50
            ;;
        2)
            echo "Servicios disponibles:"
            echo "1. auth-service"
            echo "2. user-service"
            echo "3. customer-service"
            echo "4. product-service"
            echo "5. order-service"
            echo "6. nova-gateway"
            echo "7. postgres"
            read -p "Selecciona el servicio: " service_choice
            
            case $service_choice in
                1) SERVICE="auth-service" ;;
                2) SERVICE="user-service" ;;
                3) SERVICE="customer-service" ;;
                4) SERVICE="product-service" ;;
                5) SERVICE="order-service" ;;
                6) SERVICE="nova-gateway" ;;
                7) SERVICE="postgres" ;;
                *) print_error "Opción inválida"; return ;;
            esac
            
            docker-compose logs -f --tail=50 $SERVICE
            ;;
        *)
            print_error "Opción inválida"
            ;;
    esac
}

# Ver estado
status_services() {
    print_header "Estado de servicios"
    docker-compose ps
}

# Limpiar y reiniciar
clean_and_restart() {
    print_header "Limpiando y reiniciando desde cero"
    print_info "Esto eliminará todos los contenedores y volúmenes"
    read -p "¿Está seguro? (s/n): " confirm
    
    if [[ $confirm == "s" || $confirm == "S" ]]; then
        docker-compose down -v
        print_success "Contenedores y volúmenes eliminados"
        
        print_info "Construyendo imágenes..."
        docker-compose build --no-cache
        
        print_info "Iniciando servicios..."
        docker-compose up -d
        
        print_success "Sistema reiniciado exitosamente"
        sleep 5
        status_services
    else
        print_info "Operación cancelada"
    fi
}

# Test de salud
test_health() {
    print_header "Probando salud de los servicios"
    
    services=(
        "auth-service:8080"
        "user-service:8081"
        "customer-service:8082"
        "product-service:8083"
        "order-service:8085"
        "nova-gateway:8090"
    )
    
    for service in "${services[@]}"; do
        IFS=':' read -r name port <<< "$service"
        echo -n "Probando $name ($port)... "
        
        if curl -s -f http://localhost:$port/actuator/health > /dev/null 2>&1; then
            print_success "$name está operacional"
        else
            print_error "$name no responde"
        fi
    done
    
    echo ""
    echo -n "Probando PostgreSQL... "
    if docker-compose exec -T postgres pg_isready -U postgres > /dev/null 2>&1; then
        print_success "PostgreSQL está operacional"
    else
        print_error "PostgreSQL no responde"
    fi
}

# Main loop
main() {
    print_header "Nova Commerce - Docker Management Tool"
    
    while true; do
        show_menu
        
        case $choice in
            1) build_all ;;
            2) build_single ;;
            3) up_services ;;
            4) down_services ;;
            5) restart_services ;;
            6) view_logs ;;
            7) status_services ;;
            8) clean_and_restart ;;
            9) test_health ;;
            0) print_info "Saliendo..."; exit 0 ;;
            *) print_error "Opción inválida" ;;
        esac
    done
}

# Ejecutar main
main
