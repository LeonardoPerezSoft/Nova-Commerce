# Nova Commerce - Quick Docker Reference

## 🚀 Inicio Rápido

### Windows (PowerShell)
```powershell
# Hacer el script ejecutable (primera vez)
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# Ejecutar el script
.\docker-manage.ps1
```

### Linux/Mac (Bash)
```bash
# Hacer el script ejecutable
chmod +x docker-manage.sh

# Ejecutar el script
./docker-manage.sh
```

## 📝 Comandos Directos

### Build
```bash
# Construir todas las imágenes
docker-compose build

# Construir sin cache
docker-compose build --no-cache

# Construir un servicio específico
docker-compose build auth-service
```

### Up/Down
```bash
# Iniciar servicios en background
docker-compose up -d

# Iniciar mostrando logs
docker-compose up

# Detener servicios
docker-compose down

# Detener y remover volúmenes
docker-compose down -v
```

### Logs
```bash
# Ver logs de todos
docker-compose logs -f

# Ver logs de un servicio
docker-compose logs -f auth-service

# Últimas 50 líneas
docker-compose logs --tail=50
```

### Estado
```bash
# Ver estado de todos los servicios
docker-compose ps

# Ver detalles de un contenedor
docker-compose ps auth-service
```

### Health Check
```bash
# Verificar auth-service
curl http://localhost:8080/actuator/health

# Verificar user-service
curl http://localhost:8081/actuator/health

# Verificar gateway
curl http://localhost:8090/actuator/health
```

## 🔧 Operaciones Comunes

### Reiniciar un servicio
```bash
docker-compose restart user-service
```

### Reconstruir después de cambios de código
```bash
docker-compose build auth-service
docker-compose up -d auth-service
```

### Acceder a la base de datos
```bash
docker-compose exec postgres psql -U postgres -d nova_commerce
```

### Ver recursos usados
```bash
docker stats
```

### Limpiar todo y empezar de cero
```bash
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```

## 📊 Puertos

| Servicio | Puerto | Health Check |
|----------|--------|--------------|
| Auth Service | 8080 | http://localhost:8080/actuator/health |
| User Service | 8081 | http://localhost:8081/actuator/health |
| Customer Service | 8082 | http://localhost:8082/actuator/health |
| Product Service | 8083 | http://localhost:8083/actuator/health |
| Order Service | 8085 | http://localhost:8085/actuator/health |
| API Gateway | 8090 | http://localhost:8090/actuator/health |
| PostgreSQL | 5432 | - |

## 🔐 Base de Datos

**Credenciales por defecto:**
- Usuario: `postgres`
- Contraseña: `postgres`
- Base de datos: `nova_commerce`
- Host: `postgres` (dentro de Docker)
- Host: `localhost` (desde el host)

## 📦 Variables de Entorno

Crear archivo `.env` en la raíz:

```env
JWT_SECRET=tu-secret-aqui
POSTGRES_PASSWORD=tu-contraseña-aqui
POSTGRES_USER=postgres
```

## 🐛 Troubleshooting

### Servicios no inician
```bash
# Ver logs
docker-compose logs
docker-compose logs auth-service

# Reintentar
docker-compose restart
```

### Puerto en uso
```bash
# Windows
netstat -ano | findstr :8080

# Linux/Mac
lsof -i :8080
```

### Error de conexión a BD
```bash
# Verificar PostgreSQL
docker-compose ps postgres
docker-compose logs postgres

# Reiniciar PostgreSQL
docker-compose restart postgres
```

### Limpiar images sin usar
```bash
docker image prune -a
```

## 📚 Más información

Ver [DOCKER_DEPLOYMENT.md](./DOCKER_DEPLOYMENT.md) para documentación completa.
