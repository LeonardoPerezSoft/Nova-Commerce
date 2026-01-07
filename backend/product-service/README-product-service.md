# Product Service - Nova Commerce

## 📦 Descripción

Microservicio de gestión de productos y categorías para la plataforma Nova Commerce. Implementado siguiendo Clean Architecture (Arquitectura Hexagonal) con patrón Ports & Adapters.

## 🏗️ Arquitectura

### Clean Architecture / Hexagonal

```
product-service/
├── adapter/
│   ├── in/
│   │   ├── web/            # REST Controllers
│   │   │   ├── ProductRestController
│   │   │   ├── CategoryRestController
│   │   │   └── GlobalExceptionHandler
│   │   └── filter/         # Security Filters
│   │       ├── JwtAuthenticationFilter
│   │       └── InternalApiKeyFilter
│   └── out/
│       ├── persistence/    # Database Adapters
│       │   ├── ProductPersistenceAdapter
│       │   └── CategoryPersistenceAdapter
│       └── jwt/            # JWT Validation
│           └── JwtTokenValidatorAdapter
├── application/
│   ├── port/
│   │   ├── in/            # Use Cases
│   │   │   ├── ManageProductsUseCase
│   │   │   └── ManageCategoriesUseCase
│   │   └── out/           # Persistence Ports
│   │       ├── ProductPersistencePort
│   │       └── CategoryPersistencePort
│   └── service/           # Business Logic
│       ├── ProductService
│       └── CategoryService
├── domain/
│   ├── model/             # Domain Entities
│   │   ├── Product
│   │   ├── Category
│   │   └── ProductType (enum)
│   └── exception/         # Domain Exceptions
│       ├── ProductException
│       ├── CategoryException
│       └── ResourceNotFoundException
├── repository/
│   ├── entity/            # JPA Entities
│   │   ├── ProductEntity
│   │   └── CategoryEntity
│   ├── mapper/            # Entity Mappers
│   │   ├── ProductEntityMapper
│   │   └── CategoryEntityMapper
│   ├── ProductRepository
│   └── CategoryRepository
└── config/                # Configuration
    ├── security/
    │   └── SecurityConfig
    ├── OpenApiConfig
    └── MapperConfig
```

## 🚀 Características

- ✅ **Clean Architecture**: Separación clara de responsabilidades
- ✅ **JWT Authentication**: Validación de tokens JWT desde gateway
- ✅ **Role-Based Access**: Control de acceso basado en roles (ADMIN, USER)
- ✅ **API Key Protection**: Protección de endpoints internos
- ✅ **Database Migrations**: Liquibase para versionado de esquema
- ✅ **MapStruct**: Mapeo eficiente entre capas
- ✅ **OpenAPI/Swagger**: Documentación automática de API
- ✅ **Exception Handling**: Manejo global de excepciones
- ✅ **Validation**: Validación de entrada con Bean Validation

## 📋 Modelo de Datos

### Category

| Campo       | Tipo          | Descripción              |
|-------------|---------------|--------------------------|
| id          | Long          | ID único                 |
| name        | String(100)   | Nombre (único)           |
| description | String(500)   | Descripción              |
| status      | String(20)    | ACTIVE / INACTIVE        |

### Product

| Campo         | Tipo           | Descripción                       |
|---------------|----------------|-----------------------------------|
| id            | Long           | ID único                          |
| name          | String(200)    | Nombre del producto               |
| description   | String(1000)   | Descripción                       |
| price         | BigDecimal     | Precio (>= 0)                     |
| productType   | ProductType    | PHYSICAL/DIGITAL/SERVICE/SUBSCRIPTION |
| categoryId    | Long           | FK a categories                   |
| stockQuantity | Integer        | Cantidad en stock (>= 0)          |
| status        | String(20)     | ACTIVE / INACTIVE                 |

## 🔌 API Endpoints

### Products

| Método | Endpoint                        | Roles        | Descripción                |
|--------|---------------------------------|--------------|----------------------------|
| GET    | /api/products                   | ADMIN, USER  | Listar productos (paginado)|
| GET    | /api/products/{id}              | ADMIN, USER  | Obtener producto por ID    |
| GET    | /api/products/category/{id}     | ADMIN, USER  | Productos por categoría    |
| POST   | /api/products                   | ADMIN        | Crear producto             |
| PUT    | /api/products/{id}              | ADMIN        | Actualizar producto        |
| DELETE | /api/products/{id}              | ADMIN        | Eliminar producto          |

### Categories

| Método | Endpoint                 | Roles        | Descripción                  |
|--------|--------------------------|--------------|------------------------------|
| GET    | /api/categories          | ADMIN, USER  | Listar categorías (paginado) |
| GET    | /api/categories/{id}     | ADMIN, USER  | Obtener categoría por ID     |
| POST   | /api/categories          | ADMIN        | Crear categoría              |
| PUT    | /api/categories/{id}     | ADMIN        | Actualizar categoría         |
| DELETE | /api/categories/{id}     | ADMIN        | Eliminar categoría           |

## 🛠️ Tecnologías

- **Java 17**
- **Spring Boot 3.4.3**
- **Spring Security 6.4.3** - JWT validation
- **Spring Data JPA** - Persistencia
- **PostgreSQL** - Base de datos
- **Liquibase** - Migraciones de DB
- **MapStruct 1.5.5** - Mapeo de objetos
- **Lombok** - Reducción de boilerplate
- **SpringDoc OpenAPI 2.7.0** - Documentación
- **JJWT 0.12.5** - JWT processing
- **Maven** - Build tool

## ⚙️ Configuración

### Variables de Entorno

```bash
# Database
DB_URL=jdbc:postgresql://localhost:5432/product_db
DB_USERNAME=novacommerce
DB_PASSWORD=novacommerce123
DB_POOL_SIZE=10

# Server
SERVER_PORT=8083
SPRING_PROFILE=local

# JWT
JWT_SECRET=bm92YS1jb21tZXJjZS1zdXBlci1zZWNyZXQta2V5LTIwMjQtc2VjdXJlLXNpZ24ta2V5LWZvci1qd3QtdG9rZW5z
INTERNAL_API_KEY=nova-internal-service-key-2024

# Logging
LOG_LEVEL=INFO
SQL_LOG_LEVEL=INFO
```

### Perfiles de Spring

- **local**: Desarrollo local (puerto 8083, logs DEBUG)
- **dev**: Desarrollo en servidor
- **prod**: Producción (logs WARN, pool aumentado)

## 🗄️ Base de Datos

### Crear Base de Datos

```sql
CREATE DATABASE product_db;
CREATE USER novacommerce WITH PASSWORD 'novacommerce123';
GRANT ALL PRIVILEGES ON DATABASE product_db TO novacommerce;
```

### Migraciones Liquibase

Las migraciones se ejecutan automáticamente al iniciar:

1. **001-create-category-table.yaml**: Tabla categories
2. **002-create-product-table.yaml**: Tabla products + FK
3. **003-seed-initial-data.yaml**: Datos iniciales (4 categorías, 4 productos)

## 🚀 Ejecución

### Compilar

```bash
./mvnw clean package -DskipTests
```

### Ejecutar

```bash
# Perfil local
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Con variables de entorno
SPRING_PROFILE=dev ./mvnw spring-boot:run
```

### Ejecutar JAR

```bash
java -jar target/product-service-1.0.0-SNAPSHOT.jar --spring.profiles.active=local
```

## 🔐 Seguridad

### JWT Authentication

El servicio valida tokens JWT enviados por el gateway:

```
Authorization: Bearer <JWT_TOKEN>
```

El token debe contener:
- `sub`: username
- `roles`: array de roles (ROLE_ADMIN, ROLE_USER)

### Internal API Key

Endpoints bajo `/internal/**` requieren:

```
X-Internal-API-Key: nova-internal-service-key-2024
```

## 📝 Ejemplos de Uso

### Crear Categoría (ADMIN)

```bash
curl -X POST http://localhost:8083/api/categories \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Sports",
    "description": "Sports equipment and apparel",
    "status": "ACTIVE"
  }'
```

### Crear Producto (ADMIN)

```bash
curl -X POST http://localhost:8083/api/products \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Running Shoes",
    "description": "High-performance running shoes",
    "price": 89.99,
    "productType": "PHYSICAL",
    "categoryId": 1,
    "stockQuantity": 100,
    "status": "ACTIVE"
  }'
```

### Listar Productos (USER/ADMIN)

```bash
curl -X GET "http://localhost:8083/api/products?page=0&size=10" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

### Productos por Categoría

```bash
curl -X GET "http://localhost:8083/api/products/category/1?page=0&size=10" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

## 📚 Documentación API

Swagger UI disponible en:

```
http://localhost:8083/swagger-ui.html
```

OpenAPI JSON:

```
http://localhost:8083/v3/api-docs
```

## 🔗 Integración con Gateway

El gateway (puerto 8080) debe configurar la ruta:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product-service
          uri: http://localhost:8083
          predicates:
            - Path=/api/products/**, /api/categories/**
```

## 📊 Monitoreo

### Actuator Endpoints

- Health: `http://localhost:8083/actuator/health`
- Info: `http://localhost:8083/actuator/info`
- Metrics: `http://localhost:8083/actuator/metrics`
- Prometheus: `http://localhost:8083/actuator/prometheus`

## 🧪 Testing

```bash
# Ejecutar tests
./mvnw test

# Con coverage
./mvnw verify
```

## 📦 Extensibilidad

El diseño está preparado para futuras extensiones:

- **Pricing Service**: Gestión avanzada de precios
- **Discount Service**: Promociones y descuentos
- **Inventory Service**: Control de stock avanzado
- **Review Service**: Reseñas de productos

## 👥 Roles y Permisos

| Operación                | ADMIN | USER |
|--------------------------|-------|------|
| Listar productos         | ✅     | ✅    |
| Ver producto             | ✅     | ✅    |
| Crear producto           | ✅     | ❌    |
| Actualizar producto      | ✅     | ❌    |
| Eliminar producto        | ✅     | ❌    |
| Listar categorías        | ✅     | ✅    |
| Ver categoría            | ✅     | ✅    |
| Crear categoría          | ✅     | ❌    |
| Actualizar categoría     | ✅     | ❌    |
| Eliminar categoría       | ✅     | ❌    |

## 📄 Licencia

Nova Commerce - Product Service © 2024

---

**Desarrollado con Clean Architecture para escalabilidad y mantenibilidad**
