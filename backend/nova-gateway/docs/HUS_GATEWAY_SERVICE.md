# Historias de Usuario - API Gateway (NovaCommerce)

## 📋 Épica Global
**EP-GLOBAL-001** - Plataforma de E-commerce Modular y Escalable

## 🌉 Épica API Gateway
**EP-GW-001** - Puerta de Entrada Centralizada y Segura

---

## Feature FT-GW-001 - Enrutamiento Dinámico de Microservicios

### US-GW-001: Enrutar solicitud al servicio de autenticación

**Descripción:**  
Como cliente de la API, Quiero que mis peticiones a /api/auth/** sean enrutadas automáticamente al servicio de autenticación, Para acceder a endpoints de login y registro de forma transparente.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Una solicitud HTTP a GET /api/auth/login |
| **Cuando** | El gateway recibe la petición |
| **Entonces** | La enruta al auth-service en http://localhost:8081 y retorna la respuesta al cliente |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-GW-001 - Enrutamiento Dinámico de Microservicios
- **Épica:** EP-GW-001 - Puerta de Entrada Centralizada y Segura
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

---

### US-GW-002: Enrutar solicitud al servicio de usuarios

**Descripción:**  
Como cliente de la API, Quiero que mis peticiones a /api/users/** y /api/roles/** sean enrutadas al servicio de usuarios, Para gestionar cuentas y asignación de roles.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Una solicitud HTTP a GET /api/users/{id} |
| **Cuando** | El gateway recibe la petición |
| **Entonces** | La enruta al user-service en http://localhost:8082 y retorna la respuesta |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-GW-001
- **Épica:** EP-GW-001
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

---

### US-GW-003: Enrutar solicitud al servicio de productos

**Descripción:**  
Como cliente de la API, Quiero que mis peticiones a /api/products/** y /api/categories/** sean enrutadas al servicio de productos, Para consultar catálogos y categorías.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Una solicitud HTTP a GET /api/products |
| **Cuando** | El gateway recibe la petición |
| **Entonces** | La enruta al product-service en http://localhost:8083 y retorna la respuesta |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-GW-001
- **Épica:** EP-GW-001
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

---

### US-GW-004: Enrutar solicitud al servicio de clientes

**Descripción:**  
Como cliente de la API, Quiero que mis peticiones a /api/customers/** sean enrutadas al servicio de clientes, Para gestionar perfiles y datos de cuentas.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Una solicitud HTTP a POST /api/customers |
| **Cuando** | El gateway recibe la petición |
| **Entonces** | La enruta al customer-service en http://localhost:8084 y retorna la respuesta |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-GW-001
- **Épica:** EP-GW-001
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

---

### US-GW-005: Enrutar solicitud al servicio de órdenes

**Descripción:**  
Como cliente de la API, Quiero que mis peticiones a /api/orders/** sean enrutadas al servicio de órdenes, Para crear y gestionar compras.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Una solicitud HTTP a POST /api/orders |
| **Cuando** | El gateway recibe la petición |
| **Entonces** | La enruta al order-service en http://localhost:8085 y retorna la respuesta |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-GW-001
- **Épica:** EP-GW-001
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

---

### US-GW-006: Mantener integridad de headers en tránsito

**Descripción:**  
Como filtro de gateway, Quiero limpiar headers de sesión heredados antes de enrutar, Para evitar contaminación de datos entre servicios.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Una solicitud con header Cookie |
| **Cuando** | El gateway enruta la petición |
| **Entonces** | El header Cookie es removido antes de enviar al microservicio |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-GW-001
- **Épica:** EP-GW-001
- **Dependencias:** US-GW-001
- **Versión/Release:** 1.0

---

## Feature FT-GW-002 - Autenticación y Validación de JWT

### US-GW-007: Validar JWT en rutas protegidas

**Descripción:**  
Como sistema de seguridad, Quiero validar tokens JWT antes de enrutar solicitudes, Para garantizar que solo usuarios autenticados accedan a recursos protegidos.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un request con header Authorization: Bearer {valid_token} |
| **Cuando** | Se invoca un endpoint protegido |
| **Entonces** | El filtro valida el token y permite que la solicitud continúe al microservicio |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-GW-002 - Autenticación y Validación de JWT
- **Épica:** EP-GW-001
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

---

### US-GW-008: Rechazar solicitud sin token JWT

**Descripción:**  
Como sistema de seguridad, Quiero denegar acceso a recursos protegidos sin token, Para prevenir accesos no autorizados.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un request sin header Authorization |
| **Cuando** | Se intenta acceder a una ruta protegida |
| **Entonces** | El gateway retorna HTTP 401 Unauthorized con mensaje "Missing authentication token" |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-GW-002
- **Épica:** EP-GW-001
- **Dependencias:** US-GW-007
- **Versión/Release:** 1.0

---

### US-GW-009: Rechazar token JWT expirado

**Descripción:**  
Como sistema de seguridad, Quiero invalidar tokens expirados, Para forzar re-autenticación periódica.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un request con JWT token cuya fecha de expiración ha pasado |
| **Cuando** | Se invoca un endpoint protegido |
| **Entonces** | El gateway retorna HTTP 401 Unauthorized con mensaje "Invalid or expired token" |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-GW-002
- **Épica:** EP-GW-001
- **Dependencias:** US-GW-007
- **Versión/Release:** 1.0

---

### US-GW-010: Rechazar token JWT malformado

**Descripción:**  
Como sistema de seguridad, Quiero rechazar tokens con formato inválido, Para prevenir ataques de manipulación de tokens.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un request con header Authorization: Bearer {malformed_token} |
| **Cuando** | Se invoca un endpoint protegido |
| **Entonces** | El gateway retorna HTTP 401 Unauthorized con mensaje "Invalid or expired token" |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-GW-002
- **Épica:** EP-GW-001
- **Dependencias:** US-GW-007
- **Versión/Release:** 1.0

---

### US-GW-011: Extraer claims del token JWT

**Descripción:**  
Como sistema de propagación de contexto, Quiero extraer información del usuario del token JWT, Para inyectarla en headers del microservicio destino.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un JWT token válido con claims (username, email, roles) |
| **Cuando** | El filtro valida el token |
| **Entonces** | Extrae el username del claim y lo inyecta en el header X-Username antes de enrutar |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-GW-002
- **Épica:** EP-GW-001
- **Dependencias:** US-GW-007
- **Versión/Release:** 1.0

---

## Feature FT-GW-003 - Gestión de Rutas Públicas

### US-GW-012: Permitir acceso público a endpoints de salud

**Descripción:**  
Como operador de infraestructura, Quiero permitir acceso sin autenticación a endpoints de salud, Para monitorear la disponibilidad del servicio.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Una solicitud a /actuator/health |
| **Cuando** | Se invoca sin token JWT |
| **Entonces** | El gateway permite la solicitud y retorna el estado de salud sin validar autenticación |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-GW-003 - Gestión de Rutas Públicas
- **Épica:** EP-GW-001
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

---

### US-GW-013: Permitir acceso público a documentación Swagger

**Descripción:**  
Como desarrollador, Quiero acceder a la documentación de la API sin autenticación, Para consultar los endpoints disponibles.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Una solicitud a /swagger-ui.html o /api-docs |
| **Cuando** | Se invoca sin token JWT |
| **Entonces** | El gateway permite la solicitud sin validar autenticación |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-GW-003
- **Épica:** EP-GW-001
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

---

### US-GW-014: Configurar rutas públicas dinámicamente

**Descripción:**  
Como administrador del sistema, Quiero configurar qué rutas son públicas sin modificar código, Para adaptar la seguridad a diferentes ambientes.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Una propiedad gateway.publicPaths = [/api/auth/**, /actuator/**] |
| **Cuando** | El gateway se inicia |
| **Entonces** | Las rutas especificadas se permiten sin validación JWT |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-GW-003
- **Épica:** EP-GW-001
- **Dependencias:** US-GW-012, US-GW-013
- **Versión/Release:** 1.0

---

## Feature FT-GW-004 - Control de Acceso CORS

### US-GW-015: Permitir origen válido en CORS

**Descripción:**  
Como servidor seguro, Quiero permitir solicitudes cross-origin solo desde dominios configurados, Para proteger contra ataques CSRF.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Una solicitud preflight (OPTIONS) desde http://localhost:3000 |
| **Cuando** | El servidor está configurado con allowedOrigin = http://localhost:3000 |
| **Entonces** | El gateway retorna headers CORS permitiendo el origen y métodos |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-GW-004 - Control de Acceso CORS
- **Épica:** EP-GW-001
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

---

### US-GW-016: Rechazar origen no permitido en CORS

**Descripción:**  
Como servidor seguro, Quiero denegar solicitudes cross-origin desde dominios no autorizados, Para prevenir ataques de sitios maliciosos.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Una solicitud preflight desde http://malicious-site.com |
| **Cuando** | El servidor no tiene este origen en su lista permitida |
| **Entonces** | El gateway rechaza la solicitud sin retornar headers CORS |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-GW-004
- **Épica:** EP-GW-001
- **Dependencias:** US-GW-015
- **Versión/Release:** 1.0

---

### US-GW-017: Permitir métodos HTTP configurables

**Descripción:**  
Como administrador de seguridad, Quiero especificar qué métodos HTTP son permitidos en CORS, Para restringir operaciones peligrosas.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | allowedMethods = [GET, POST, PUT] |
| **Cuando** | Se recibe un OPTIONS con Access-Control-Request-Method: DELETE |
| **Entonces** | El gateway no incluye DELETE en los headers CORS permitidos |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-GW-004
- **Épica:** EP-GW-001
- **Dependencias:** US-GW-015
- **Versión/Release:** 1.0

---

## Feature FT-GW-005 - Manejo de Errores y Respuestas

### US-GW-018: Retornar error 401 con mensaje estructurado

**Descripción:**  
Como cliente de la API, Quiero recibir errores 401 con mensajes claros, Para entender por qué me rechazó el gateway.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Una solicitud sin token |
| **Cuando** | Se invoca una ruta protegida |
| **Entonces** | Retorna HTTP 401 con body JSON { "error": "Missing authentication token", "timestamp": "..." } |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-GW-005 - Manejo de Errores y Respuestas
- **Épica:** EP-GW-001
- **Dependencias:** US-GW-008
- **Versión/Release:** 1.0

---

### US-GW-019: Enrutar respuesta de error del microservicio

**Descripción:**  
Como cliente de la API, Quiero recibir los mensajes de error originales del microservicio, Para tener contexto completo de lo que ocurrió.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | El microservicio retorna HTTP 400 con mensaje de validación |
| **Cuando** | El gateway recibe la respuesta |
| **Entonces** | Enruta la respuesta de error del microservicio sin modificarla al cliente |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-GW-005
- **Épica:** EP-GW-001
- **Dependencias:** US-GW-001
- **Versión/Release:** 1.0

---

### US-GW-020: Registrar errores en logs

**Descripción:**  
Como operador de infraestructura, Quiero tener trazabilidad de intentos de acceso no autorizados, Para auditar y detectar patrones de ataque.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario intenta acceder sin token a /api/orders |
| **Cuando** | El filtro valida la solicitud |
| **Entonces** | Se registra en logs: WARN "No JWT token found in request for protected path: /api/orders" |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-GW-005
- **Épica:** EP-GW-001
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

---

## 📊 Matriz de Cobertura: Tests vs Historias de Usuario

| US | Título | Test Unitario | Test Integración | ✅ |
|---|---|---|---|---|
| US-GW-001 | Enrutar a auth-service | GatewayConfigTest | N/A | ✅ |
| US-GW-002 | Enrutar a user-service | GatewayConfigTest | N/A | ✅ |
| US-GW-003 | Enrutar a product-service | GatewayConfigTest | N/A | ✅ |
| US-GW-004 | Enrutar a customer-service | GatewayConfigTest | N/A | ✅ |
| US-GW-005 | Enrutar a order-service | GatewayConfigTest | N/A | ✅ |
| US-GW-006 | Remover headers Cookie | GatewayConfigTest | N/A | ✅ |
| US-GW-007 | Validar JWT válido | JwtTokenValidatorAdapterTest | JwtAuthenticationFilterTest | ✅ |
| US-GW-008 | Rechazar sin token | JwtAuthenticationFilterTest | N/A | ✅ |
| US-GW-009 | Rechazar token expirado | JwtTokenValidatorAdapterTest | JwtAuthenticationFilterTest | ✅ |
| US-GW-010 | Rechazar token malformado | JwtTokenValidatorAdapterTest | N/A | ✅ |
| US-GW-011 | Extraer claims del token | JwtTokenValidatorAdapterTest | JwtAuthenticationFilterTest | ✅ |
| US-GW-012 | Permitir /actuator/health | SecurityPropertiesTest | N/A | ✅ |
| US-GW-013 | Permitir /swagger-ui | SecurityPropertiesTest | N/A | ✅ |
| US-GW-014 | Configurar rutas públicas | SecurityPropertiesTest | N/A | ✅ |
| US-GW-015 | Permitir origen CORS | CorsConfigTest | N/A | ✅ |
| US-GW-016 | Rechazar origen CORS no válido | CorsConfigTest | N/A | ✅ |
| US-GW-017 | Métodos CORS configurables | CorsConfigTest | N/A | ✅ |
| US-GW-018 | Respuesta error 401 | JwtAuthenticationFilterTest | N/A | ✅ |
| US-GW-019 | Enrutar error de microservicio | GatewayConfigTest | N/A | ✅ |
| US-GW-020 | Registrar en logs | JwtAuthenticationFilterTest | N/A | ✅ |

---

## 📐 Resumen INVEST

| Principio | Cumplimiento | Observación |
|---|---|---|
| **I**ndependent | ✅✅✅ | Rutas independientes, CORS aislado, JWT validable sin servicios externos |
| **N**egotiable | ✅✅ | Sin detalles de algoritmo criptográfico, enfoque en interfaces |
| **V**aluable | ✅✅✅ | Cada HU tiene "Para" claro, 5 actores diferentes (clientes, operadores, desarrolladores) |
| **E**stimable | ✅✅✅ | Criterios concretos, endpoints específicos, validaciones cuantificables |
| **S**mall | ✅✅✅ | Sprint-sized (2-5 puntos cada una), 20 HU ÷ 5 sprints |
| **T**estable | ✅✅✅ | Escenarios con métodos HTTP, headers específicos, códigos de respuesta verificables |

---

## 🏗️ Notas Arquitectónicas

- **Global Filter Pattern**: JwtAuthenticationFilter actúa antes de enrutamiento, sin modificar servicios backend
- **Configuration Properties**: SecurityProperties permite rutas públicas configurables sin recompilación
- **Token Validator Port**: Abstracción de JJWT (JwtTokenValidatorAdapter) permitiendo cambios de librería sin afectar filtro
- **Header Utilities**: Extracción y propagación de información de seguridad encapsulada en HeaderUtils
- **Route Locator**: Spring Cloud Gateway RouteLocator define ruta a cada microservicio con prefijo dinámico
- **CORS Filter**: WebFilter dedicado para manejo estándar de cross-origin, aplicable globalmente
- **Error Handling**: Respuestas JSON estructuradas con timestamp para trazabilidad
- **Zero-Trust Architecture**: Cada solicitud valida token; no se confía en headers internos sin validación

---

## 🔐 Principios de Seguridad

1. **Validación de entrada**: Paths públicos evaluados con AntPathMatcher
2. **JWT Stateless**: Sin sesiones, escalable horizontalmente
3. **Header Injection**: Usuario autenticado inyectado en X-Username al microservicio
4. **CORS Whitelist**: Solo orígenes autorizados acceden; métodos restringibles
5. **Logging de intentos fallidos**: Trazabilidad de accesos no autorizados para auditoría
6. **Token Expiration**: Expiración configurable fuerza re-autenticación periódica
