# API Gateway 

## Descripción

API Gateway central para la plataforma ecommerce Nova Commerce. Proporciona un punto único de entrada para todos los microservicios, validación de JWT local y enrutamiento inteligente.

### Arquitectura de Microservicios

```
Cliente → API Gateway (8080) → Auth-Service (8081) → (Feign) → User-Service (8082)
                              → User-Service (8082)
```

- **Auth-Service**: Genera JWT mediante validación con User-Service vía Feign Client
- **User-Service**: Gestiona usuarios, roles y permisos (base de datos PostgreSQL)
- **API Gateway**: Valida JWT y enruta a servicios backend (los servicios confían en el gateway)

## Características

- ✅ Punto único de entrada (Single Entry Point)
- ✅ Validación LOCAL de JWT (sin llamadas remotas)
- ✅ Enrutamiento hacia microservicios
- ✅ Inyección de headers personalizados (X-Username, X-Authorities)
- ✅ Configuración CORS
- ✅ Rutas públicas y protegidas
- ✅ Programación reactiva con WebFlux
- ✅ **Clean Architecture** con separación de capas

## Tecnologías

- Spring Boot 3.5.9
- Spring Cloud Gateway
- Java 17
- Maven
- JWT (JJWT 0.12.5)
- Project Reactor (WebFlux)
- Lombok

## Estructura del Proyecto - Clean Architecture

```
com.novacommerce.gateway/
│
├── domain/                          ← CAPA DE DOMINIO
│   ├── model/
│   │   └── Token.java              # Entidad de dominio para Token JWT
│   └── exception/
│       └── AuthenticationException  # Excepciones de dominio
│
├── application/                     ← CAPA DE APLICACIÓN
│   └── port/
│       └── TokenValidatorPort.java # Puerto (interfaz) de validación
│
├── adapter/                         ← CAPA DE ADAPTADORES
│   ├── in/                          # Adaptadores de entrada (HTTP)
│   │   └── filter/
│   │       └── JwtAuthenticationFilter  # Filtro global de autenticación
│   │
│   └── out/                         # Adaptadores de salida (dependencias)
│       ├── jwt/
│       │   └── JwtTokenValidatorAdapter  # Implementación de validación JWT
│       └── header/
│           └── HeaderUtils          # Utilidades para gestión de headers
│
├── config/                          ← CAPA DE CONFIGURACIÓN
│   ├── GatewayConfig.java          # Configuración de rutas
│   ├── SecurityConfig.java         # Configuración de seguridad
│   ├── CorsConfig.java             # Configuración CORS
│   └── SecurityProperties.java     # Properties de seguridad
│
├── routing/
│   └── RouteConstants.java         # Constantes de enrutamiento
│
└── NovaGatewayApplication.java     # Punto de entrada
```

### Explicación de Clean Architecture

#### **Domain Layer (Dominio)**
- Define las entidades y reglas de negocio puras
- Independiente de frameworks y bases de datos
- `Token`: modelo que representa un token validado
- `AuthenticationException`: excepciones específicas del dominio

#### **Application Layer (Aplicación)**
- Define los puertos (interfaces)
- `TokenValidatorPort`: contrato para validar tokens
- Independiente de la implementación

#### **Adapter Layer (Adaptadores)**
- Implementa los puertos de la capa de aplicación
- `JwtTokenValidatorAdapter`: implementación JJWT del puerto
- `JwtAuthenticationFilter`: adaptador de entrada (filtro HTTP)
- `HeaderUtils`: adaptador para gestión de headers

#### **Framework Layer (Configuración)**
- Configuración de Spring, rutas, CORS
- Wiring de beans
- Propiedades de la aplicación

### Ventajas de esta Arquitectura

✅ **Independencia de Framework**: El dominio no depende de Spring  
✅ **Testabilidad**: Fácil crear mocks de puertos  
✅ **Mantenibilidad**: Responsabilidades claramente definidas  
✅ **Extensibilidad**: Fácil agregar nuevas implementaciones

## Configuración

### Variables de Entorno

```bash
JWT_SECRET=E8F7D6C4B5A3F2E1D9C8B7A6F5E4D3C2B1A0F9E8D7C6B5A4F3E2D1C0B9A8F7E6D5C4B3A2F1E0D9C8B7A6F5E4D3C2B1A0F9E8D7C6B5A4
```

**IMPORTANTE**: Este SECRET_KEY debe ser el mismo en auth-service y api-gateway.

### application.yaml

El archivo `application.yaml` contiene:
- Configuración del servidor (puerto 8080)
- Configuración JWT (secret, expiración)
- Rutas de microservicios
- Rutas públicas (sin autenticación)
- Configuración CORS
- Configuración de Actuator
- Niveles de logging

## Rutas

### Rutas Públicas (sin JWT)

- `/api/auth/login` - Login de usuarios
- `/api/auth/refresh` - Renovación de tokens
- `/swagger-ui/**` - Documentación Swagger
- `/v3/api-docs/**` - OpenAPI docs

### Rutas Protegidas (con JWT)

#### Auth Service (8081)
- `/api/auth/**` - Endpoints de autenticación

#### User Service (8082)
- `/api/users/**` - Gestión de usuarios
- `/api/roles/**` - Gestión de roles y permisos

Todas las rutas protegidas requieren un JWT válido en el header `Authorization: Bearer <token>`.

## Flujo de Autenticación

### 1. Validación de JWT (Adapter In - Filtro)

El `JwtAuthenticationFilter` (adapter de entrada) intercepta todas las requests:

```
Request → JwtAuthenticationFilter (adapter/in/filter)
          ├─ Verifica si es ruta pública
          │  └─ Sí: permite paso directo
          └─ No: valida token
             ├─ Extrae token del header Authorization: Bearer <token>
             ├─ Delega validación al puerto TokenValidatorPort
             ├─ El adapter JWT implementa el puerto
             └─ Si es válido:
                 ├─ Extrae username y authorities
                 ├─ Inyecta headers X-Username y X-Authorities
                 └─ Continúa hacia microservicio backend
                 Si es inválido: retorna 401 Unauthorized (JSON)
```

**Flujo de capas:**
```
Adapter In (Filtro HTTP)
    ↓ (delega a)
Application Port (TokenValidatorPort)
    ↓ (implementado por)
Adapter Out (JwtTokenValidatorAdapter)
    ↓ (usa)
Domain (Token model, AuthenticationException)
```

### 2. Enrutamiento (Adapter Gateway)

El `GatewayConfig` define las rutas hacia los microservicios:

```yaml
/api/auth/**  → http://localhost:8081 (auth-service)
/api/users/** → http://localhost:8082 (user-service)
/api/roles/** → http://localhost:8082 (user-service)
```

### 3. Respuestas de Error

En caso de error de autenticación, se retorna JSON estructurado:

```json
{
  "error": "Unauthorized",
  "message": "Invalid or expired token",
  "status": 401,
  "timestamp": "2026-01-06T10:30:45",
  "path": "/api/users/123"
}
```

## Ejecución

### Compilar

```bash
./mvnw clean package
```

### Ejecutar

```bash
./mvnw spring-boot:run
```

O con variable de entorno personalizada:

```bash
JWT_SECRET=tu-secreto-personalizado ./mvnw spring-boot:run
```

### Ejecutar JAR

```bash
java -jar target/api-gateway-0.0.1-SNAPSHOT.jar
```

## Pruebas

### Endpoint público (sin token)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### Endpoint protegido (con token)

```bash
curl http://localhost:8080/api/protected/resource \
  -H "Authorization: Bearer <tu-token-jwt>"
```

## Integración con Microservicios

### Arquitectura de Confianza

Los microservicios backend (user-service, etc.) **confían completamente en el API Gateway**:

- ✅ Gateway valida JWT y autorización
- ✅ Servicios backend NO validan JWT (evita duplicación)
- ✅ Servicios backend permiten `/api/**` sin autenticación
- ✅ Comunicación interna (Feign) usa endpoints `/internal/**`

### Headers Personalizados (Opcional)

Si se implementan filtros personalizados, el gateway puede inyectar:

- `X-Username`: Nombre del usuario autenticado
- `X-Authorities`: Lista de authorities separadas por comas

Ejemplo en un controller:

```java
@GetMapping("/resource")
public ResponseEntity<?> getResource(
    @RequestHeader(value = "X-Username", required = false) String username
) {
    // Lógica de negocio
}
```

## Agregar Nuevos Microservicios

1. Actualizar `application.yaml`:

```yaml
gateway:
  routes:
    nuevo-service:
      uri: http://localhost:8083
      path: /api/nuevo/**
```

2. Actualizar `GatewayConfig.java`:

```java
@Value("${gateway.routes.nuevo-service.uri}")
private String nuevoServiceUri;

@Value("${gateway.routes.nuevo-service.path}")
private String nuevoServicePath;

// En customRouteLocator():
.route("nuevo-service", r -> r
    .path(nuevoServicePath)
    .filters(f -> f
        .stripPrefix(0)
        .removeRequestHeader("Cookie"))
    .uri(nuevoServiceUri))
```

3. **Importante**: El servicio backend NO debe validar JWT, debe confiar en el gateway:
   - Comentar `@EnableMethodSecurity` en SecurityConfig
   - Permitir `/api/**` en SecurityFilterChain

## Monitoreo

Actuator endpoints disponibles:

- `http://localhost:8080/actuator/health` - Estado de salud
- `http://localhost:8080/actuator/info` - Información del servicio
- `http://localhost:8080/actuator/metrics` - Métricas

## Seguridad

- ✅ JWT validado localmente (sin latencia de red)
- ✅ Secret key configurable por variable de entorno
- ✅ Tokens expirados rechazados automáticamente
- ✅ Sin almacenamiento de credenciales
- ✅ Headers de seguridad inyectados

## Autor

**Nova Commerce**  
Plataforma Nova Commerce

## Licencia

Propietario - Leonardo Pérez
