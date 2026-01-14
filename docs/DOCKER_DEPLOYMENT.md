# Nova Commerce - Docker Deployment Guide

Este documento explica cómo contenerizar y desplegar todos los servicios de Nova Commerce usando Docker y Docker Compose.

## 📋 Estructura de Servicios

```
Backend Services:
├── auth-service          (Puerto 8080)
├── user-service          (Puerto 8081)
├── customer-service      (Puerto 8082)
├── product-service       (Puerto 8083)
├── order-service         (Puerto 8085)
└── nova-gateway          (Puerto 8090)

Infrastructure:
└── PostgreSQL            (Puerto 5432)
```

## 🚀 Requisitos Previos

- Docker Engine 20.10+
- Docker Compose 2.0+
- Git

## 📦 Construcción de Imágenes

### Opción 1: Construir todas las imágenes a la vez

```bash
cd /path/to/Nova-Commerce
docker-compose build
```

### Opción 2: Construir una imagen específica

```bash
# Build auth-service
docker-compose build auth-service

# Build user-service
docker-compose build user-service
```

### Opción 3: Construir sin cache

```bash
docker-compose build --no-cache
```

## ▶️ Iniciar los Servicios

### Iniciar todos los servicios en background

```bash
docker-compose up -d
```

### Iniciar mostrando logs en tiempo real

```bash
docker-compose up
```

### Iniciar servicios específicos

```bash
docker-compose up -d auth-service user-service
```

## 🛑 Detener los Servicios

### Detener todos los servicios

```bash
docker-compose down
```

### Detener y remover volúmenes

```bash
docker-compose down -v
```

### Detener servicios específicos

```bash
docker-compose stop auth-service user-service
```

## 📊 Monitoreo y Logs

### Ver logs de todos los servicios

```bash
docker-compose logs -f
```

### Ver logs de un servicio específico

```bash
docker-compose logs -f auth-service
docker-compose logs -f user-service
```

### Ver últimas N líneas de logs

```bash
docker-compose logs --tail=50
```

## 🔍 Verificar Estado de Servicios

```bash
docker-compose ps
```

Salida esperada:
```
NAME                    COMMAND                 SERVICE          STATUS         PORTS
nova-auth-service       "java -Dspring.prof…"   auth-service     Up 2 minutes   0.0.0.0:8080->8080/tcp
nova-user-service       "java -Dspring.prof…"   user-service     Up 2 minutes   0.0.0.0:8081->8081/tcp
nova-customer-service   "java -Dspring.prof…"   customer-service Up 2 minutes   0.0.0.0:8082->8082/tcp
nova-product-service    "java -Dspring.prof…"   product-service  Up 2 minutes   0.0.0.0:8083->8083/tcp
nova-order-service      "java -Dspring.prof…"   order-service    Up 2 minutes   0.0.0.0:8085->8085/tcp
nova-gateway            "java -Dspring.prof…"   nova-gateway     Up 2 minutes   0.0.0.0:8090->8090/tcp
nova-postgres           "docker-entrypoint.s…"  postgres         Up 3 minutes   0.0.0.0:5432->5432/tcp
```

## 🔐 Variables de Entorno

Las siguientes variables de entorno se pueden configurar:

### JWT Secret (Opcional)

```bash
export JWT_SECRET="tu-secret-base64-aqui"
docker-compose up -d
```

Si no se especifica, se usa un secret por defecto (solo para desarrollo).

### Crear archivo `.env`

```bash
# .env file
JWT_SECRET=dGhpc2lzYXZlcnlzZWN1cmVrZXlmb3JqV1RoYXNiZWVuZ2VuZXJhdGVkZm9ydGVzdGluZ3B1cnBvc2VzYW5kaXNub3Rmb3Jwcm9kdWN0aW9u
POSTGRES_PASSWORD=postgres
POSTGRES_USER=postgres
```

Luego ejecutar:
```bash
docker-compose --env-file .env up -d
```

## 🧪 Pruebas

### Verificar que los servicios están respondiendo

```bash
# Auth Service
curl -X GET http://localhost:8080/actuator/health

# User Service
curl -X GET http://localhost:8081/actuator/health

# Customer Service
curl -X GET http://localhost:8082/actuator/health

# Product Service
curl -X GET http://localhost:8083/actuator/health

# Order Service
curl -X GET http://localhost:8085/actuator/health

# API Gateway
curl -X GET http://localhost:8090/actuator/health
```

## 🗄️ Base de Datos

### Acceder a PostgreSQL

```bash
docker-compose exec postgres psql -U postgres -d nova_commerce
```

### Comandos útiles en PostgreSQL

```sql
-- Ver todas las tablas
\dt

-- Ver estructura de una tabla
\d nombre_tabla

-- Salir
\q
```

## 🔧 Troubleshooting

### El servicio no inicia

```bash
# Ver logs detallados
docker-compose logs [servicio-name]

# Reiniciar un servicio
docker-compose restart [servicio-name]
```

### Error de puerto en uso

```bash
# Ver qué proceso usa el puerto
netstat -ano | findstr :8080  # Windows
lsof -i :8080                 # Linux/Mac

# O cambiar el puerto en docker-compose.yml
```

### Error de conexión a base de datos

```bash
# Verificar que PostgreSQL está corriendo
docker-compose ps postgres

# Ver logs de PostgreSQL
docker-compose logs postgres
```

### Limpiar y reiniciar desde cero

```bash
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```

## 📈 Escalado de Servicios

### Escalar un servicio a múltiples instancias

```bash
docker-compose up -d --scale user-service=3
```

⚠️ **Nota:** Para escalar servicios, debe haber un load balancer (ej: nginx) delante.

## 🚢 Despliegue en Producción

### Consideraciones importantes:

1. **Cambiar secrets y contraseñas** en el archivo `.env`
2. **Usar volúmenes persistentes** para PostgreSQL
3. **Configurar un reverse proxy** (nginx, traefik)
4. **Habilitar SSL/TLS**
5. **Monitoreo y logging** (ELK, Prometheus)
6. **Backups automatizados** de la base de datos

### Ejemplo de configuración avanzada:

```yaml
version: '3.9'

services:
  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
    depends_on:
      - nova-gateway
    networks:
      - nova-network

  # ... resto de servicios
```

## 📚 Recursos Adicionales

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [Best Practices for Java Images](https://github.com/GoogleContainerTools/distroless)

## 📝 Notas

- Los contenedores se reinician automáticamente si se detienen (`restart: unless-stopped`)
- La base de datos PostgreSQL persiste en un volumen Docker
- Todos los servicios están en la red `nova-network` para comunicación interna
- Los servicios esperan conectarse a `postgres:5432` como host de BD

---

**Última actualización:** 13 de enero de 2026
