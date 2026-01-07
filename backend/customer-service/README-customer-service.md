# Customer Service - Nova Commerce

Microservicio de gestión de clientes para Nova Commerce.

Implementa Clean Architecture con separación entre dominio, aplicación y adaptadores. Persiste en PostgreSQL mediante JPA/Hibernate y gestiona el esquema con Liquibase. Expone API REST protegida por JWT y validaciones de rol.

```
Cliente → API Gateway → Customer-Service → PostgreSQL
                    ↓
                Swagger / OpenAPI
```

## 📋 Requisitos

- Java 17+
- Maven 3.6+
- PostgreSQL 14+ (local o Docker)
- IDE: IntelliJ IDEA o VS Code

## 🚀 Instalación y Configuración

### 1. Clonar el repositorio
```bash
git clone https://github.com/LeonardoPerezSoft/Nova-Commerce.git
cd backend/customer-service
```

### 2. Base de datos (Docker opcional)
```powershell
docker run -d --name nova-postgres `
  -e POSTGRES_USER=postgres `
  -e POSTGRES_PASSWORD=postgres `
  -e POSTGRES_DB=nova_db `
  -p 5432:5432 postgres:16
```

### 3. Variables de entorno (opcional)
Puedes sobrescribir las propiedades de `application.yaml`:
```bash
SPRING_PROFILES_ACTIVE=local
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/nova_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# Semilla de datos
SEED_ENABLED=true

# Seguridad JWT
JWT_SECRET=E8F7D6C4B5A3F2E1D9C8B7A6F5E4D3C2B1A0F9E8D7C6B5A4F3E2D1C0B9A8F7E6D5C4B3A2F1E0D9C8B7A6F5E4D3C2B1A0F9E8D7C6B5A4
INTERNAL_API_KEY=nova-internal-service-key-2024
```

### 4. Compilar y ejecutar
```powershell
mvnw.cmd clean compile -DskipTests
mvnw.cmd spring-boot:run -DskipTests
```
Por defecto, el servicio corre en el puerto 8084 (ver configuración en [backend/customer-service/src/main/resources/application.yaml](backend/customer-service/src/main/resources/application.yaml)).

## 🛠️ Tecnologías

- Spring Boot 3.x
- Spring Data JPA (Hibernate)
- Liquibase
- Spring Security (JWT)
- SpringDoc OpenAPI (Swagger UI)
- Lombok
- MapStruct
- Java 17

## 📚 Documentación de API

- Swagger UI: http://localhost:8084/swagger-ui.html
- OpenAPI JSON: http://localhost:8084/v3/api-docs

## 📁 Estructura del Proyecto - Clean Architecture

```
com.novacommerce.customer_service/
│
├── domain/                          ← Dominio
│   └── model/                       # Customer, CustomerStatus
│
├── application/                     ← Aplicación
│   ├── port/
│   │   ├── in/                      # ManageCustomersUseCase, ManageCustomerStatusUseCase
│   │   └── out/                     # CustomerPersistencePort
│   └── service/                     # CustomerService
│
├── adapter/                         ← Adaptadores
│   ├── in/
│   │   └── web/
│   │       ├── CustomerRestController.java
│   │       ├── dto/                 # CustomerDto
│   │       └── mapper/              # CustomerMapper
│   └── out/                         # Persistencia/Integraciones (si aplica)
│
├── config/                          ← Configuración
│   └── SecurityConfig.java          # Filtro JWT + llave interna
│
└── resources/
    ├── application.yaml             # Puerto 8084, datasource, springdoc
    └── db/liquibase/
        ├── changelog-master.yaml
        ├── 001-create-customers-table.yaml
        └── 002-seed-customers.yaml
```

Archivos relevantes:
- Configuración general: [backend/customer-service/src/main/resources/application.yaml](backend/customer-service/src/main/resources/application.yaml)
- Controlador REST: [backend/customer-service/src/main/java/com/novacommerce/customer_service/adapter/in/web/CustomerRestController.java](backend/customer-service/src/main/java/com/novacommerce/customer_service/adapter/in/web/CustomerRestController.java)
- Liquibase master: [backend/customer-service/src/main/resources/db/liquibase/changelog-master.yaml](backend/customer-service/src/main/resources/db/liquibase/changelog-master.yaml)
- Creación de tabla: [backend/customer-service/src/main/resources/db/liquibase/001-create-customers-table.yaml](backend/customer-service/src/main/resources/db/liquibase/001-create-customers-table.yaml)
- Seed de clientes: [backend/customer-service/src/main/resources/db/liquibase/002-seed-customers.yaml](backend/customer-service/src/main/resources/db/liquibase/002-seed-customers.yaml)
- Filtro API interna: [backend/customer-service/src/main/java/com/novacommerce/customer_service/adapter/in/security/InternalApiKeyFilter.java](backend/customer-service/src/main/java/com/novacommerce/customer_service/adapter/in/security/InternalApiKeyFilter.java)

## 🔐 Seguridad

- Autenticación mediante JWT (Bearer token) y autorización por roles.
- Roles usados: `ROLE_ADMIN`, `ROLE_READ`, `ROLE_CREATE`, `ROLE_UPDATE`, `ROLE_DELETE`.
- Rutas públicas: `/v3/api-docs/**`, `/swagger-ui.html`, `/swagger-ui/**`, `/actuator/health`, `/actuator/info`.
- Rutas internas: cualquier ruta que empiece por `/internal/**` requiere el header `X-Internal-API-Key` (ver `InternalApiKeyFilter`).

## 🧭 Endpoints

### Customers
- GET `/api/customers` — Listar clientes
- GET `/api/customers/{id}` — Obtener cliente por ID
- POST `/api/customers` — Crear cliente
- PUT `/api/customers/{id}` — Actualizar cliente
- DELETE `/api/customers/{id}` — Eliminar cliente

## 📋 Ejemplos de uso

### Listar clientes
```bash
curl -X GET http://localhost:8084/api/customers \
  -H "Authorization: Bearer <your_access_token>"
```

### Crear cliente
```bash
curl -X POST http://localhost:8084/api/customers \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_access_token>" \
  -d '{
    "firstName": "Alice",
    "lastName": "García",
    "email": "alice@nova.com",
    "phone": "+57 3001112233",
    "status": "ACTIVE",
    "loyaltyLevel": "BRONZE"
  }'
```

### Ruta interna (ejemplo)
```bash
curl -X GET http://localhost:8084/internal/health \
  -H "X-Internal-API-Key: nova-internal-service-key-2024"
```

> Nota: Las rutas internas dependen de los endpoints definidos bajo `/internal/**` en tu proyecto. El filtro valida el header de llave interna.

## 🧪 Pruebas

```powershell
mvnw.cmd test
```

## ⚙️ Perfiles

```powershell
mvnw.cmd spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

## 🔍 Troubleshooting

### "release version 17 not supported"
Configura Java 17 en Windows:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
java -version
mvnw.cmd clean compile
mvnw.cmd package -DskipTests
```

### "Connection refused: PostgreSQL"
- Verifica que el contenedor/servicio PostgreSQL esté activo en `localhost:5432`.
- Revisa credenciales y `SPRING_DATASOURCE_URL`.

### "Port 8084 already in use"
Cambia el puerto en `application.yaml`:
```yaml
server:
  port: 9094
```

## 📞 Soporte

Para soporte y preguntas:
- Email: yesid.perez@sofka.com.co
- Web: https://www.novacommerce.com

---

Desarrollado con ❤️ por **Leonardo Pérez**