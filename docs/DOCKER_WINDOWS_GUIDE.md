# 🪟 Guía de Despliegue Docker para Windows

## Requisitos Previos

### Instalación de Docker Desktop

1. **Descargar Docker Desktop para Windows**
   - Ir a: https://www.docker.com/products/docker-desktop
   - Descargar "Docker Desktop for Windows"

2. **Requisitos de Sistema**
   - Windows 10 Pro/Enterprise o Windows 11
   - Virtualización habilitada en BIOS
   - Mínimo 4GB RAM (8GB recomendado)
   - 10GB espacio libre en disco

3. **Instalación**
   - Ejecutar instalador descargado
   - Seguir los pasos del asistente
   - Reiniciar la computadora
   - Abrir PowerShell como Administrador

4. **Verificación de Instalación**
   ```powershell
   docker --version
   docker-compose --version
   docker run hello-world
   ```

## Habilitar PowerShell Scripts

### Primera vez (IMPORTANTE)

```powershell
# Abrir PowerShell como Administrador

# Ver política actual
Get-ExecutionPolicy

# Permitir scripts locales
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# Confirmar (escribir 'Y' y Enter)
# Verificar cambio
Get-ExecutionPolicy
```

## Uso de los Scripts de Gestión

### Opción 1: Script Interactivo (Recomendado)

```powershell
# Navegar a la carpeta del proyecto
cd C:\ruta\a\Nova-Commerce

# Ejecutar el script
.\docker-manage.ps1

# Seleccionar opción del menú (1-9, 0 para salir)
```

**Menú disponible:**
```
1. Build - Construir todas las imágenes (primera vez)
2. Build Single - Construir una imagen específica
3. Up - Iniciar todos los servicios ⭐ (usar esto)
4. Down - Detener todos los servicios
5. Restart - Reiniciar servicios
6. Logs - Ver logs en tiempo real
7. Status - Ver estado de servicios
8. Clean - Limpiar y reiniciar desde cero
9. Test Health - Probar salud de servicios
0. Salir
```

### Opción 2: Comandos Directos

```powershell
# Ir a la carpeta del proyecto
cd C:\Nova-Commerce

# Construir imágenes (primera vez, tarda ~15-30 min)
docker-compose build

# Iniciar todos los servicios
docker-compose up -d

# Ver estado
docker-compose ps

# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down

# Limpiar todo
docker-compose down -v
```

## Configuración Inicial

### 1. Crear archivo .env

```powershell
# En la carpeta del proyecto
copy .env.example .env

# Editar con Notepad o VS Code
notepad .env
```

**Valores a cambiar:**
```env
POSTGRES_PASSWORD=tu_contraseña_segura_aqui
JWT_SECRET=tu_secret_base64_aqui (mínimo 88 caracteres)
```

### 2. Verificar Puertos Disponibles

```powershell
# Verificar puerto 8080
netstat -ano | findstr :8080

# Verificar puerto 8081
netstat -ano | findstr :8081

# Verificar todos los puertos
netstat -ano | findstr :808
netstat -ano | findstr :809
netstat -ano | findstr :5432
```

**Si dice "No se encontraron entradas" = Puerto disponible ✅**

Si algún puerto está en uso:
1. Cambiar el puerto en `docker-compose.yml`
2. O liberar el puerto (deteniendo la aplicación que lo usa)

## Workflow Típico

### Primer Despliegue (25-40 minutos)

```powershell
# 1. Abrir PowerShell como Administrador
# 2. Ir a carpeta del proyecto
cd C:\Nova-Commerce

# 3. Crear configuración
copy .env.example .env
# Editar .env con contraseña segura

# 4. Ejecutar script
.\docker-manage.ps1

# 5. Esperar a que complete (tarda tiempo en primera compilación)

# 6. Seleccionar opción "3" para iniciar servicios

# 7. Esperar 30-60 segundos a que inicien todos
# Ver mensaje "Started XxxServiceApplication in X.XXX seconds" para cada uno

# 8. Seleccionar opción "9" para probar salud

# 9. Todos en status UP ✅ Completado!
```

### Uso Diario (1-2 minutos)

```powershell
# Iniciar servicios
.\docker-manage.ps1
# Seleccionar "3"

# ... trabajar ...

# Ver logs
.\docker-manage.ps1
# Seleccionar "6"

# Detener servicios
.\docker-manage.ps1
# Seleccionar "4"
```

## Troubleshooting en Windows

### Problema: PowerShell no ejecuta el script

**Error:** `File C:\...\docker-manage.ps1 cannot be loaded`

**Solución:**
```powershell
# Ejecutar como Administrador:
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# Confirmar con 'Y' y Enter
```

### Problema: Docker no inicia/no responde

**Solución:**
1. Abrir "Services" (services.msc)
2. Buscar "Docker Desktop Service"
3. Click derecho → Reiniciar
4. O reiniciar toda la computadora

### Problema: Puertos en uso

**Ver qué ocupa el puerto:**
```powershell
# Buscar proceso en puerto 8080
netstat -ano | findstr :8080

# Resultado: TCP    127.0.0.1:8080    0.0.0.0:0    LISTENING    12345
# El número 12345 es el PID del proceso

# Encontrar el proceso
tasklist | findstr 12345

# Detenerlo (si es necesario)
taskkill /PID 12345 /F

# O cambiar el puerto en docker-compose.yml
# De:   ports: - "8080:8080"
# A:    ports: - "9090:8080"
```

### Problema: "Docker daemon is not running"

**Solución:**
```powershell
# Docker Desktop debe estar abierto
# Si está cerrado, abrirlo desde:
# Inicio → Docker Desktop

# O ejecutar:
"C:\Program Files\Docker\Docker\Docker Desktop.exe"

# Esperar a que cargue (~30 segundos)

# Verificar:
docker ps
```

### Problema: Espacio en disco bajo

```powershell
# Ver uso de espacio en Docker
docker system df

# Limpiar imágenes sin usar
docker image prune -a

# Limpiar volúmenes sin usar
docker volume prune

# Limpiar todo
docker system prune -a
```

### Problema: "Cannot connect to PostgreSQL"

```powershell
# Ver logs de PostgreSQL
docker-compose logs postgres

# Reiniciar PostgreSQL
docker-compose restart postgres

# Si persiste:
docker-compose down
docker-compose up -d postgres
```

## Monitoring en Windows

### Ver recursos en tiempo real

```powershell
# Ver CPU y memoria por contenedor
docker stats

# Ver logs de un servicio específico
docker-compose logs -f auth-service
docker-compose logs -f user-service
docker-compose logs -f postgres

# Ver eventos
docker events
```

## Acceder a Docker Desktop UI

Windows 10/11 incluye una interfaz gráfica:

1. Abrir **Docker Desktop**
2. Ir a pestaña **Containers**
3. Ver todos los servicios corriendo
4. Click en un contenedor para ver detalles
5. Ver logs en tiempo real

## Integración con VS Code

### Extensión Docker para VS Code

1. Abrir VS Code
2. Ir a Extensiones (Ctrl+Shift+X)
3. Buscar "Docker"
4. Instalar "Docker" por Microsoft

**Funcionalidades:**
- Ver contenedores corriendo
- Ver logs
- Ejecutar comandos
- Crear archivos
- Build/Push de imágenes

## Backup de la Base de Datos

```powershell
# Hacer backup de PostgreSQL
docker-compose exec postgres pg_dump -U postgres nova_commerce > backup_$(Get-Date -Format yyyyMMdd_HHmmss).sql

# Restaurar desde backup
cat backup_20260113_120000.sql | docker-compose exec -T postgres psql -U postgres nova_commerce
```

## Acceder a PostgreSQL en Windows

### Opción 1: Desde PowerShell (CLI)

```powershell
# Conectarse a PostgreSQL dentro del contenedor
docker-compose exec postgres psql -U postgres -d nova_commerce

# Comandos útiles:
# \dt              - ver todas las tablas
# \d nombre_tabla  - ver estructura de tabla
# SELECT * FROM usuarios LIMIT 5;  - ver datos
# \q              - salir
```

### Opción 2: Con GUI (pgAdmin)

```powershell
# Agregar a docker-compose.yml:
# services:
#   pgadmin:
#     image: dpage/pgadmin4
#     ports:
#       - "5050:80"
#     environment:
#       PGADMIN_DEFAULT_EMAIL: admin@admin.com
#       PGADMIN_DEFAULT_PASSWORD: admin

# Acceder a: http://localhost:5050
```

## Producción en Windows Server

Para Windows Server (versión 2016+):

```powershell
# Instalar Docker Enterprise Edition (ahora Docker Desktop para Windows Server)
# O usar WSL 2 (Windows Subsystem for Linux)

# Instalación de WSL 2:
wsl --install

# Luego instalar Docker Desktop con WSL 2 backend
```

## Notas Importantes para Windows

⚠️ **Paths:** Usar `\` o `/` indistintamente en docker-compose.yml

⚠️ **Línea de comandos:** Las rutas pueden variar según terminal (cmd vs PowerShell)

⚠️ **Memoria:** Docker usa Hyper-V, asignar suficiente RAM en Docker Desktop Settings

⚠️ **Antivirus:** Algunas funciones de Antivirus pueden interferir con Docker

⚠️ **Windows Defender:** Excluir la carpeta del proyecto de escaneo en tiempo real

## Soporte Adicional

- 📖 Documentación Docker: https://docs.docker.com/desktop/install/windows-install/
- 🔗 Foro Docker Community: https://community.docker.com/
- 📧 Soporte Docker: https://www.docker.com/support/

---

**Última actualización:** 13 de enero de 2026
**Versión:** Docker Desktop 4.20+
**Compatible:** Windows 10/11, Windows Server 2016+
