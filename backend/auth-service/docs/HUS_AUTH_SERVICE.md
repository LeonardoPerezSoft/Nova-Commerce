# Historias de Usuario - Auth Service (NovaCommerce)

## 📋 Épica Global
**EP-GLOBAL-001** - Plataforma de E-commerce Modular y Escalable

## 🔐 Épica Auth Service
**EP-AUTH-001** - Sistema de Autenticación y Autorización Centralizada

---

## Feature FT-AUTH-001 - Autenticación de Usuarios

### US-AUTH-001: Login con credenciales válidas

**Descripción:**  
Como usuario del sistema, Quiero autenticarme con mi username/email y contraseña, Para obtener tokens JWT que me permitan acceder a los servicios protegidos de la plataforma.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario existente con estatus ACTIVE y credenciales correctas |
| **Cuando** | Se invoca POST /api/auth/login con userIdentifier y password válidos |
| **Entonces** | El sistema retorna HTTP 200 con accessToken, refreshToken, tokenType "bearer" y roles/permisos del usuario |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-AUTH-001 - Autenticación de Usuarios
- **Épica:** EP-AUTH-001 - Sistema de Autenticación
- **Dependencias:** User-Service debe estar activo en puerto 8082
- **Versión/Release:** 1.0

**Detalles Técnicos:**
- Utiliza Feign Client para validar credenciales contra User-Service
- Genera JWT con JJWT 0.11.5 usando algoritmo HS512
- Access Token: 24 horas de validez
- Refresh Token: 7 días de validez

---

### US-AUTH-002: Rechazar login con credenciales inválidas

**Descripción:**  
Como sistema de seguridad, Quiero rechazar intentos de login con credenciales incorrectas, Para proteger las cuentas de usuario contra accesos no autorizados.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un intento de login con password incorrecta |
| **Cuando** | Se invoca POST /api/auth/login con credenciales inválidas |
| **Entonces** | Se rechaza con HTTP 401 y excepción InvalidCredentialsException sin revelar si el usuario existe |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-AUTH-001
- **Épica:** EP-AUTH-001
- **Dependencias:** US-AUTH-001
- **Versión/Release:** 1.0

**Detalles Técnicos:**
- Mensaje genérico: "Usuario o contraseña inválidos"
- No revela si el problema es usuario inexistente o password incorrecta (seguridad)
- Log de intento fallido para auditoría

---

### US-AUTH-003: Rechazar login de usuario inactivo

**Descripción:**  
Como sistema de seguridad, Quiero rechazar el acceso a usuarios deshabilitados, Para prevenir accesos no autorizados de cuentas suspendidas.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario con enabled = false |
| **Cuando** | Se intenta autenticar |
| **Entonces** | Se rechaza con HTTP 401 y excepción InvalidCredentialsException indicando "Usuario deshabilitado" |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-AUTH-001
- **Épica:** EP-AUTH-001
- **Dependencias:** US-AUTH-001
- **Versión/Release:** 1.0

**Detalles Técnicos:**
- User-Service retorna UserValidationResponse con enabled=false
- Auth-Service valida este campo antes de generar tokens

---

### US-AUTH-004: Rechazar login de usuario bloqueado

**Descripción:**  
Como sistema de seguridad, Quiero rechazar el acceso a usuarios bloqueados, Para prevenir accesos de cuentas comprometidas o con actividad sospechosa.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario con locked = true |
| **Cuando** | Se intenta autenticar |
| **Entonces** | Se rechaza con HTTP 401 y excepción InvalidCredentialsException indicando "Usuario bloqueado" |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-AUTH-001
- **Épica:** EP-AUTH-001
- **Dependencias:** US-AUTH-001
- **Versión/Release:** 1.0

**Detalles Técnicos:**
- User-Service retorna UserValidationResponse con locked=true
- Auth-Service valida este campo antes de generar tokens
- Requiere intervención administrativa para desbloquear

---

### US-AUTH-005: Validar formato de credenciales en request

**Descripción:**  
Como sistema de entrada, Quiero validar que los campos de login cumplan con los requisitos mínimos, Para evitar procesamiento innecesario de solicitudes inválidas.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un LoginRequest con campos vacíos o nulos |
| **Cuando** | Se invoca POST /api/auth/login |
| **Entonces** | Se rechaza con HTTP 400 y MethodArgumentNotValidException detallando los campos inválidos |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-AUTH-001
- **Épica:** EP-AUTH-001
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

**Detalles Técnicos:**
- Utiliza Jakarta Validation (@NotBlank)
- Validaciones en LoginRequest:
  - userIdentifier: no nulo, no vacío
  - password: no nulo, no vacío
- GlobalExceptionHandler maneja MethodArgumentNotValidException

---

## Feature FT-AUTH-002 - Gestión de Tokens JWT

### US-AUTH-006: Generar tokens JWT con información del usuario

**Descripción:**  
Como sistema de autorización, Quiero incluir roles y permisos en el JWT, Para que otros microservicios puedan tomar decisiones de autorización sin consultar la base de datos.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Una autenticación exitosa de usuario con roles y permisos |
| **Cuando** | Se genera el access token |
| **Entonces** | El JWT contiene claims: username, authorities (roles con prefijo ROLE_ + permisos), iat, exp |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-AUTH-002 - Gestión de Tokens JWT
- **Épica:** EP-AUTH-001
- **Dependencias:** US-AUTH-001
- **Versión/Release:** 1.0

**Detalles Técnicos:**
- Algoritmo: HS512
- Secret: mínimo 512 bits (64 bytes)
- Claim "authorities": roles (prefijo ROLE_) + permisos separados por comas
- Ejemplo: "ROLE_ADMIN,USER_CREATE,USER_DELETE"
- Tiempo de expiración: 24 horas (configurable)

---

### US-AUTH-007: Refrescar access token con refresh token válido

**Descripción:**  
Como usuario autenticado, Quiero obtener un nuevo access token sin reingresar credenciales, Para mantener mi sesión activa sin interrupciones.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un refresh token válido y no expirado |
| **Cuando** | Se invoca POST /api/auth/refresh con el refreshToken |
| **Entonces** | Se retorna HTTP 200 con nuevo accessToken, refreshToken, roles y permisos actualizados |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-AUTH-002
- **Épica:** EP-AUTH-001
- **Dependencias:** US-AUTH-001, US-AUTH-006
- **Versión/Release:** 1.0

**Detalles Técnicos:**
- Valida el refresh token contra la firma JWT
- Extrae username y authorities del refresh token
- Genera nuevos access y refresh tokens
- Refresh token: 7 días de validez (configurable)

---

### US-AUTH-008: Rechazar refresh token inválido o expirado

**Descripción:**  
Como sistema de seguridad, Quiero rechazar refresh tokens comprometidos o expirados, Para prevenir accesos no autorizados con tokens robados.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un refresh token malformado, con firma inválida o expirado |
| **Cuando** | Se invoca POST /api/auth/refresh |
| **Entonces** | Se rechaza con HTTP 401 y excepción InvalidTokenException |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-AUTH-002
- **Épica:** EP-AUTH-001
- **Dependencias:** US-AUTH-007
- **Versión/Release:** 1.0

**Detalles Técnicos:**
- Valida firma JWT con el secret compartido
- Verifica expiración contra timestamp actual
- Rechaza tokens con claims vacíos o authorities nulas
- Mensajes específicos: "Token JWT inválido", "Token JWT expirado"

---

### US-AUTH-009: Validar token JWT y extraer información

**Descripción:**  
Como microservicio protegido, Quiero validar tokens JWT y obtener la información del usuario, Para implementar autorización a nivel de endpoint sin llamar a User-Service.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un access token válido en el header Authorization: Bearer <token> |
| **Cuando** | Se invoca GET /api/auth/validate |
| **Entonces** | Retorna HTTP 200 con TokenValidationResponse: valid=true, username, authorities |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-AUTH-002
- **Épica:** EP-AUTH-001
- **Dependencias:** US-AUTH-006
- **Versión/Release:** 1.0

**Detalles Técnicos:**
- Endpoint protegido: requiere JWT válido
- Extrae token del header Authorization
- Valida firma y expiración
- Retorna TokenValidationResponse con información del usuario

---

### US-AUTH-010: Retornar validación inválida para tokens comprometidos

**Descripción:**  
Como sistema de seguridad, Quiero retornar una respuesta de validación negativa para tokens inválidos, Para que los servicios consumidores rechacen el acceso.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un token inválido, expirado o sin header Authorization |
| **Cuando** | Se invoca GET /api/auth/validate |
| **Entonces** | Retorna HTTP 200 con TokenValidationResponse: valid=false, username=null, authorities=null |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-AUTH-002
- **Épica:** EP-AUTH-001
- **Dependencias:** US-AUTH-009
- **Versión/Release:** 1.0

**Detalles Técnicos:**
- No lanza excepción, retorna respuesta con valid=false
- Permite a API Gateway decidir cómo manejar el rechazo
- Log de intento de validación fallido

---

## Feature FT-AUTH-003 - Seguridad y Filtros JWT

### US-AUTH-011: Filtrar requests con JWT en header Authorization

**Descripción:**  
Como sistema de seguridad, Quiero interceptar todas las solicitudes HTTP y validar el JWT automáticamente, Para proteger endpoints sin código duplicado en cada controller.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Una request a un endpoint protegido con header Authorization: Bearer <token> válido |
| **Cuando** | La request pasa por JwtAuthenticationFilter |
| **Entonces** | Se extrae el token, se valida, y se establece el Authentication en SecurityContext para la request |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-AUTH-003 - Seguridad y Filtros JWT
- **Épica:** EP-AUTH-001
- **Dependencias:** US-AUTH-006
- **Versión/Release:** 1.0

**Detalles Técnicos:**
- Implementa OncePerRequestFilter
- Extrae token del header Authorization (Bearer prefix)
- Valida token usando JwtTokenProvider
- Crea UsernamePasswordAuthenticationToken con authorities
- Establece Authentication en SecurityContextHolder

---

### US-AUTH-012: Permitir acceso público a endpoints de autenticación

**Descripción:**  
Como sistema de configuración, Quiero permitir acceso sin autenticación a /api/auth/login y /api/auth/refresh, Para que los usuarios puedan autenticarse sin tener un token previo.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Una request a POST /api/auth/login o POST /api/auth/refresh |
| **Cuando** | Se procesa la request |
| **Entonces** | No se requiere token JWT, los endpoints son públicos (permitAll) |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-AUTH-003
- **Épica:** EP-AUTH-001
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

**Detalles Técnicos:**
- SecurityConfig define reglas con HttpSecurity
- Endpoints públicos: POST /api/auth/login, POST /api/auth/refresh
- Swagger UI también es público: /swagger-ui/**, /v3/api-docs/**
- Endpoint protegido: GET /api/auth/validate (requiere authenticated())

---

### US-AUTH-013: Configurar sesión stateless para JWT

**Descripción:**  
Como arquitecto de seguridad, Quiero configurar Spring Security en modo stateless, Para que no se creen sesiones HTTP y cada request sea independiente con su token JWT.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Una aplicación configurada con SessionCreationPolicy.STATELESS |
| **Cuando** | Se procesa cualquier request |
| **Entonces** | No se crea HttpSession, toda la autenticación depende del token JWT en cada request |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-AUTH-003
- **Épica:** EP-AUTH-001
- **Dependencias:** US-AUTH-011
- **Versión/Release:** 1.0

**Detalles Técnicos:**
- SecurityConfig establece sessionCreationPolicy(SessionCreationPolicy.STATELESS)
- CSRF deshabilitado (no necesario en API REST stateless)
- JwtAuthenticationFilter se ejecuta antes de UsernamePasswordAuthenticationFilter

---

### US-AUTH-014: Manejar errores de autenticación y autorización

**Descripción:**  
Como sistema de manejo de errores, Quiero retornar respuestas JSON consistentes para errores de autenticación y autorización, Para facilitar el debugging y mejorar la experiencia del desarrollador.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un error de autenticación (401) o acceso denegado (403) |
| **Cuando** | Se produce la excepción |
| **Entonces** | Se retorna ErrorResponse JSON con timestamp, status, error, message, path y opcionalmente fieldErrors |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-AUTH-003
- **Épica:** EP-AUTH-001
- **Dependencias:** US-AUTH-001, US-AUTH-009
- **Versión/Release:** 1.0

**Detalles Técnicos:**
- GlobalExceptionHandler maneja todas las excepciones
- Excepciones manejadas:
  - InvalidCredentialsException → 401
  - InvalidTokenException → 401
  - AccessDeniedException → 403
  - AuthenticationException → 401
  - MethodArgumentNotValidException → 400
  - ResourceNotFoundException → 404
  - DuplicateResourceException → 409
  - Exception genérica → 500
- SecurityConfig define AuthenticationEntryPoint y AccessDeniedHandler personalizados

---

## Feature FT-AUTH-004 - Integración con User Service

### US-AUTH-015: Validar credenciales contra User Service vía Feign

**Descripción:**  
Como Auth Service, Quiero delegar la validación de credenciales a User Service, Para mantener una única fuente de verdad sobre usuarios y no duplicar datos.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un LoginRequest con userIdentifier y password |
| **Cuando** | Se procesa la autenticación |
| **Entonces** | Se invoca POST /api/internal/users/validate-credentials en User-Service con header X-Internal-API-Key |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-AUTH-004 - Integración con User Service
- **Épica:** EP-AUTH-001
- **Dependencias:** User-Service corriendo en puerto 8082
- **Versión/Release:** 1.0

**Detalles Técnicos:**
- UserServiceAdapter implementa UserValidationPort
- Utiliza UserServiceClient (Feign interface)
- Envía UserValidationRequest con userIdentifier y password
- Recibe UserValidationResponse con username, email, enabled, locked, roles, permissions
- Header X-Internal-API-Key para autenticación interna

---

### US-AUTH-016: Manejar errores de comunicación con User Service

**Descripción:**  
Como sistema resiliente, Quiero manejar errores de comunicación con User Service, Para proporcionar mensajes de error útiles cuando el servicio no está disponible.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | User Service no está disponible o retorna error 5xx |
| **Cuando** | Se intenta validar credenciales |
| **Entonces** | Se lanza excepción con mensaje descriptivo y se retorna HTTP 500 o 503 según corresponda |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-AUTH-004
- **Épica:** EP-AUTH-001
- **Dependencias:** US-AUTH-015
- **Versión/Release:** 1.0

**Detalles Técnicos:**
- UserServiceAdapter captura FeignException
- Propaga excepciones apropiadas según status code:
  - 401 → InvalidCredentialsException
  - 503 → ServiceUnavailableException
  - Otros → RuntimeException genérica
- GlobalExceptionHandler maneja todas las excepciones

---

### US-AUTH-017: Prefixar roles con ROLE_ para Spring Security

**Descripción:**  
Como sistema de autorización compatible con Spring Security, Quiero prefixar todos los roles con "ROLE_", Para que las anotaciones @PreAuthorize funcionen correctamente en otros microservicios.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario con rol "ADMIN" en User-Service |
| **Cuando** | Se genera el JWT |
| **Entonces** | El claim "authorities" contiene "ROLE_ADMIN" (con prefijo agregado por Auth-Service) |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-AUTH-004
- **Épica:** EP-AUTH-001
- **Dependencias:** US-AUTH-006, US-AUTH-015
- **Versión/Release:** 1.0

**Detalles Técnicos:**
- AuthService agrega prefijo "ROLE_" a todos los roles
- Permisos NO llevan prefijo
- Ejemplo authorities: "ROLE_ADMIN,ROLE_USER,USER_CREATE,ORDER_READ"
- Compatibilidad con @PreAuthorize("hasRole('ADMIN')")

---

## Feature FT-AUTH-005 - Configuración y Documentación

### US-AUTH-018: Documentar API con OpenAPI/Swagger

**Descripción:**  
Como desarrollador consumidor, Quiero acceder a documentación interactiva de la API, Para entender cómo autenticarme y utilizar los endpoints sin leer código.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Auth-Service corriendo en localhost:8081 |
| **Cuando** | Accedo a http://localhost:8081/swagger-ui.html |
| **Entonces** | Veo documentación completa de todos los endpoints con esquemas de request/response y ejemplos |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-AUTH-005 - Configuración y Documentación
- **Épica:** EP-AUTH-001
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

**Detalles Técnicos:**
- SpringDoc OpenAPI 2.7.0
- OpenApiConfig define información del servicio
- Swagger UI: http://localhost:8081/swagger-ui.html
- OpenAPI JSON: http://localhost:8081/v3/api-docs
- Seguridad: Bearer Token definido como securityScheme

---

### US-AUTH-019: Configurar JWT secret compartido con API Gateway

**Descripción:**  
Como arquitecto de seguridad, Quiero que el JWT secret sea configurable vía variables de entorno, Para compartir la misma clave entre Auth-Service y API Gateway sin hardcodearla.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | JWT_SECRET definido en variable de entorno o application.yaml |
| **Cuando** | Se inicia Auth-Service |
| **Entonces** | JwtTokenProvider utiliza el secret configurado para firmar y validar tokens |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-AUTH-005
- **Épica:** EP-AUTH-001
- **Dependencias:** US-AUTH-006
- **Versión/Release:** 1.0

**Detalles Técnicos:**
- Configuración en application.yaml: app.jwt.secret
- Soporta Base64 encoding del secret
- Mínimo 512 bits para HS512
- Mismo secret en auth-service y api-gateway (crítico)
- Tiempo de expiración configurable: app.jwt.expiration, app.jwt.refresh-expiration

---

### US-AUTH-020: Implementar Clean Architecture con puertos y adaptadores

**Descripción:**  
Como arquitecto de software, Quiero separar la lógica de negocio de los detalles de infraestructura, Para facilitar testing, mantenibilidad y evolución del código.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | La estructura del proyecto siguiendo Clean Architecture |
| **Cuando** | Se requiere cambiar la implementación de JWT o Feign |
| **Entonces** | Solo se modifican los adaptadores (JwtTokenAdapter, UserServiceAdapter) sin tocar la lógica de negocio (AuthService) |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-AUTH-005
- **Épica:** EP-AUTH-001
- **Dependencias:** Todas las US
- **Versión/Release:** 1.0

**Detalles Técnicos:**
- **Application Layer**: AuthService, Use Cases (interfaces), Ports
- **Adapter Layer IN**: AuthRestController (REST HTTP)
- **Adapter Layer OUT**: JwtTokenAdapter (JWT), UserServiceAdapter (Feign)
- **Dependencias**: Application Layer independiente de frameworks
- **Testing**: Fácil mockear puertos (TokenGeneratorPort, UserValidationPort)

---

## 📊 Matriz de Trazabilidad

| Feature | Historia de Usuario | Prioridad | Estado | Tests |
|---------|---------------------|-----------|--------|-------|
| FT-AUTH-001 | US-AUTH-001 a US-AUTH-005 | Alta | ✅ Implementado | ✅ 100% |
| FT-AUTH-002 | US-AUTH-006 a US-AUTH-010 | Alta | ✅ Implementado | ✅ 100% |
| FT-AUTH-003 | US-AUTH-011 a US-AUTH-014 | Alta | ✅ Implementado | ✅ 95% |
| FT-AUTH-004 | US-AUTH-015 a US-AUTH-017 | Alta | ✅ Implementado | ✅ 100% |
| FT-AUTH-005 | US-AUTH-018 a US-AUTH-020 | Media | ✅ Implementado | ✅ 93% |

---

## 🔗 Dependencias Externas

### Dependencias de Microservicios
- **User-Service** (puerto 8082): Validación de credenciales, obtención de roles/permisos
- **API Gateway** (puerto 8080): Debe compartir el mismo JWT_SECRET para validar tokens

### Dependencias Técnicas
- **Spring Boot**: 3.4.3
- **Spring Cloud**: 2024.0.0
- **Spring Cloud OpenFeign**: 4.3.0
- **Spring Security**: 6.4.3
- **JJWT**: 0.11.5
- **Lombok**: Para reducción de boilerplate
- **MapStruct**: 1.5.5 (mapeo de DTOs)
- **SpringDoc OpenAPI**: 2.7.0
- **Java**: 17

---

## 📝 Notas Técnicas Importantes

### Seguridad
- ⚠️ **JWT Secret**: Debe ser mínimo 512 bits (64 bytes) para HS512
- ✅ **Stateless**: No se usan sesiones HTTP, todo basado en tokens
- ✅ **CSRF**: Deshabilitado (no necesario en API REST stateless)
- ✅ **Internal API Key**: Header X-Internal-API-Key para comunicación con User-Service

### Tokens
- **Access Token**: 24 horas de validez (configurable)
- **Refresh Token**: 7 días de validez (configurable)
- **Algoritmo**: HS512
- **Claims**: username, authorities, iat, exp

### Arquitectura
- ✅ **Clean Architecture**: Separación entre dominio, aplicación y adaptadores
- ✅ **Hexagonal**: Puertos (interfaces) y adaptadores (implementaciones)
- ✅ **Testing**: 93% de cobertura total, 100% en componentes críticos
- ✅ **Sin Base de Datos**: Auth-Service no persiste datos, delega a User-Service

---

## 🚀 Roadmap Futuro

### Versión 1.1 (Próximas features)
- **US-AUTH-021**: Implementar revocación de tokens (blacklist)
- **US-AUTH-022**: Soporte para OAuth2/OIDC
- **US-AUTH-023**: Autenticación de dos factores (2FA)
- **US-AUTH-024**: Rate limiting en endpoints de login
- **US-AUTH-025**: Auditoría de intentos de login fallidos

### Versión 2.0 (Mejoras arquitectónicas)
- **US-AUTH-026**: Implementar refresh token rotation
- **US-AUTH-027**: Soporte para múltiples tenants
- **US-AUTH-028**: Integración con proveedores externos (Google, Facebook)
- **US-AUTH-029**: Token introspection endpoint
- **US-AUTH-030**: Almacenamiento de tokens en Redis

---

## 📚 Referencias

- [README Auth Service](../README-AUTH-SERVICE.md)
- [OpenAPI Specification](http://localhost:8081/v3/api-docs)
- [Swagger UI](http://localhost:8081/swagger-ui.html)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
