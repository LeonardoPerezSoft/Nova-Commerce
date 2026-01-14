# 📋 Checklist de Despliegue Docker - Nova Commerce

## Pre-Despliegue - Verificaciones Iniciales

### Requisitos del Sistema
- [ ] Docker Desktop instalado (versión 20.10+)
- [ ] Docker Compose instalado (versión 2.0+)
- [ ] PowerShell 5.1+ (Windows) o Bash (Linux/Mac)
- [ ] Git instalado
- [ ] Mínimo 4GB RAM disponibles
- [ ] Mínimo 10GB espacio libre en disco

### Verificación de Instalación
```bash
# Verificar Docker
docker --version
# Esperado: Docker version 20.10.0 o superior

# Verificar Docker Compose
docker-compose --version
# Esperado: Docker Compose version 2.0.0 o superior
```

## Preparación - Antes de Iniciar

### 1. Configuración de Variables de Entorno
- [ ] Copiar `.env.example` a `.env`
- [ ] Cambiar `POSTGRES_PASSWORD` por contraseña segura
- [ ] Generar nuevo `JWT_SECRET` seguro
- [ ] Revisar otros valores según ambiente

### 2. Verificar Puertos Disponibles
```bash
# Windows PowerShell
netstat -ano | findstr :8080
netstat -ano | findstr :8081
netstat -ano | findstr :8082
netstat -ano | findstr :8083
netstat -ano | findstr :8085
netstat -ano | findstr :8090
netstat -ano | findstr :5432

# Linux/Mac
lsof -i :8080
lsof -i :8081
lsof -i :8082
lsof -i :8083
lsof -i :8085
lsof -i :8090
lsof -i :5432
```

Puertos requeridos:
- [ ] 8080 - Auth Service
- [ ] 8081 - User Service
- [ ] 8082 - Customer Service
- [ ] 8083 - Product Service
- [ ] 8085 - Order Service
- [ ] 8090 - API Gateway
- [ ] 5432 - PostgreSQL

### 3. Verificar Configuración Git
- [ ] Repositorio actualizado (`git pull`)
- [ ] Sin cambios locales sin commitear
- [ ] Rama correcta (develop/main)

## Construcción - Compilar Imágenes

### 4. Build de Imágenes
```bash
cd /ruta/a/Nova-Commerce

# Opción A: Script interactivo (Recomendado)
# Windows
.\docker-manage.ps1  # Seleccionar opción 1

# Linux/Mac
./docker-manage.sh   # Seleccionar opción 1

# Opción B: Comando directo
docker-compose build

# Opción C: Build sin cache (si hay problemas)
docker-compose build --no-cache
```

Verificación:
- [ ] Build completado sin errores
- [ ] Imágenes creadas exitosamente
- [ ] No hay warnings críticos

**Comando de verificación:**
```bash
docker images | grep nova
```

Debería mostrar:
```
nova-commerce-auth-service          latest
nova-commerce-user-service          latest
nova-commerce-customer-service      latest
nova-commerce-product-service       latest
nova-commerce-order-service         latest
nova-commerce-nova-gateway          latest
```

## Despliegue - Iniciar Servicios

### 5. Iniciar Servicios
```bash
# Opción A: Script interactivo
# Windows
.\docker-manage.ps1  # Seleccionar opción 3

# Linux/Mac
./docker-manage.sh   # Seleccionar opción 3

# Opción B: Comando directo
docker-compose up -d

# Opción C: Con logs en consola (desarrollo)
docker-compose up  # Presionar Ctrl+C para detener
```

Verificación:
- [ ] Comando ejecutado sin errores
- [ ] Contenedores iniciando

### 6. Esperar a que los Servicios Estén Listos
```bash
# Esperar 30-60 segundos para compilación de BD

# Ver estado en tiempo real
docker-compose ps

# Verificar logs
docker-compose logs -f
```

Esperar a ver mensajes como:
```
auth-service | ... Started AuthServiceApplication in X.XXX seconds
user-service | ... Started UserServiceApplication in X.XXX seconds
...
```

## Validación - Verificar Funcionamiento

### 7. Health Checks
```bash
# Opción A: Script interactivo
# Windows
.\docker-manage.ps1  # Seleccionar opción 9

# Linux/Mac
./docker-manage.sh   # Seleccionar opción 9

# Opción B: Comandos manuales
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8085/actuator/health
curl http://localhost:8090/actuator/health
```

Respuesta esperada para cada uno:
```json
{"status":"UP"}
```

Verificación:
- [ ] Auth Service: UP
- [ ] User Service: UP
- [ ] Customer Service: UP
- [ ] Product Service: UP
- [ ] Order Service: UP
- [ ] API Gateway: UP
- [ ] PostgreSQL: UP

### 8. Verificar Conectividad de Base de Datos
```bash
docker-compose exec postgres psql -U postgres -d nova_commerce -c "SELECT 1"
```

Respuesta esperada:
```
 ?column?
----------
        1
(1 row)
```

Verificación:
- [ ] Conexión a PostgreSQL exitosa
- [ ] Base de datos nova_commerce creada

### 9. Prueba de Endpoints API

#### Autenticación (Auth Service)
```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'
```

- [ ] Respuesta exitosa con token JWT

#### Usuarios (User Service)
```bash
# Obtener usuarios (requiere token)
curl -X GET http://localhost:8081/api/users \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

- [ ] Respuesta con lista de usuarios

#### Órdenes (Order Service)
```bash
# Obtener órdenes (requiere token ADMIN)
curl -X GET http://localhost:8085/api/orders \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

- [ ] Respuesta exitosa (o 403 si no es admin)

## Post-Despliegue - Tareas Finales

### 10. Logs y Monitoreo
```bash
# Ver logs de todos los servicios
docker-compose logs -f --tail=100

# Ver logs de un servicio específico
docker-compose logs -f auth-service

# Verificar uso de recursos
docker stats
```

Verificación:
- [ ] No hay errores críticos en logs
- [ ] Uso de CPU y memoria razonable
- [ ] Todas las aplicaciones inicializadas correctamente

### 11. Documentar Configuración
- [ ] Guardar archivo `.env` de forma segura
- [ ] Documentar cualquier configuración custom
- [ ] Guardar variables de entorno importantes

### 12. Backup y Recuperación
```bash
# Hacer backup de la base de datos
docker-compose exec postgres pg_dump -U postgres nova_commerce > backup.sql
```

- [ ] Backup de BD realizado
- [ ] Archivo guardado en ubicación segura

## Mantenimiento Continuo

### Tareas Regulares
- [ ] Revisar logs diariamente
- [ ] Verificar uso de recursos semanalmente
- [ ] Hacer backups de BD semanalmente
- [ ] Actualizar dependencias mensualmente
- [ ] Probar recuperación ante desastres mensualmente

### Monitoreo en Producción
```bash
# Ver estado en tiempo real
docker-compose ps

# Ver métricas
docker stats

# Ver eventos recientes
docker events
```

## Troubleshooting Rápido

### Problema: Puertos en uso
```bash
# Buscar qué ocupa el puerto
# Windows: netstat -ano | findstr :8080
# Linux: lsof -i :8080

# Solución: Cambiar puerto en docker-compose.yml
```
- [ ] Puerto identificado y liberado

### Problema: Servicios no inician
```bash
# Ver logs detallados
docker-compose logs [servicio]

# Reintentar inicio
docker-compose restart

# Si persiste, limpiar y reconstruir
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```
- [ ] Servicios reiniciados exitosamente

### Problema: Conexión a BD rechazada
```bash
# Verificar PostgreSQL
docker-compose ps postgres
docker-compose logs postgres

# Reiniciar BD
docker-compose restart postgres

# Esperar health check
```
- [ ] PostgreSQL operacional

### Problema: Bajo rendimiento
```bash
# Ver recursos
docker stats

# Aumentar recursos si es necesario
# En docker-compose.yml agregar:
# services:
#   [servicio]:
#     mem_limit: 512m
#     cpus: '0.5'
```
- [ ] Recursos asignados correctamente

## Lista de Verificación Final

### Antes de considerar el despliegue completado:
- [ ] Todos los servicios en estado UP
- [ ] Health checks retornan status UP
- [ ] Pruebas de API exitosas
- [ ] Base de datos operacional
- [ ] Logs sin errores críticos
- [ ] Backup realizado
- [ ] Variables de entorno seguras
- [ ] Documentación actualizada
- [ ] Equipo de operaciones notificado
- [ ] Plan de rollback documentado

## 🎯 Estado: LISTO PARA PRODUCCIÓN

Cuando todos los puntos estén marcados, el sistema está listo para:
- ✅ Tráfico de producción
- ✅ Múltiples usuarios
- ✅ Escalado horizontal
- ✅ Monitoreo 24/7

---

**Última actualización:** 13 de enero de 2026
**Responsable:** DevOps Team
**Próxima revisión:** [Fecha]
