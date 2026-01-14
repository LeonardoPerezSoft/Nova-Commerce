# 🏗️ Arquitectura y Diseño en Nova Commerce

## Índice

1. [Principios SOLID](#principios-solid)
2. [Patrones de Diseño](#patrones-de-diseño)
3. [Clean Code](#clean-code)
4. [Conclusiones](#conclusiones)

---

## Principios SOLID

Los **principios SOLID** son 5 directrices para escribir código mantenible, extensible y testeable. Nova Commerce implementa todos ellos correctamente.

### 1️⃣ S - Single Responsibility Principle (SRP)

**Definición:** Una clase debe tener una única razón para cambiar. Debe tener una única responsabilidad.

#### ✅ Ejemplo 1: Separación de Responsabilidades en Servicios

**Ubicación:** [user-service/application/service/UserService.java](backend/user-service/src/main/java/com/novacommerce/user_service/application/service/UserService.java)

```java
/**
 * Servicio de aplicación que implementa los casos de uso de gestión de usuarios.
 * Orquesta las operaciones usando los puertos definidos (Clean Architecture).
 * 
 * RESPONSABILIDAD ÚNICA: Gestionar usuarios
 * No maneja: seguridad, persistencia, mapeo de DTOs (lo hacen otros componentes)
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService implements ManageUsersUseCase, ValidateUserCredentialsUseCase {
    
    private final UserPersistencePort userPersistencePort;
    private final RolePersistencePort rolePersistencePort;
    private final PasswordEncoderPort passwordEncoderPort;
    
    /**
     * Crea un nuevo usuario.
     * RESPONSABILIDAD: Lógica de negocio de creación
     * DELEGACIÓN:
     * - Persistencia → userPersistencePort
     * - Carga de roles → rolePersistencePort
     * - Codificación de password → passwordEncoderPort
     */
    @Override
    public User createUser(User user, Set<UUID> roleIds) {
        // Validaciones de negocio
        if (userPersistencePort.existsByUsername(user.getUsername())) {
            throw new DuplicateResourceException("User", "username", user.getUsername());
        }
        
        // Cargar roles (delegado)
        Set<Role> roles = loadRoles(roleIds);
        user.setRoles(roles);
        
        // Codificar password (delegado)
        String encodedPassword = passwordEncoderPort.encode(user.getPassword());
        user.setPassword(encodedPassword);
        
        // Persistir (delegado)
        return userPersistencePort.save(user);
    }
    
    /**
     * RAZONES para cambiar UserService:
     * 1. Cambiar lógica de validación de usuario
     * 2. Cambiar reglas de negocio de creación
     * 
     * NO cambiarán por:
     * - Cambiar BD (responsabilidad de UserPersistencePort)
     * - Cambiar algoritmo de hash (responsabilidad de PasswordEncoderPort)
     * - Cambiar mapeo de DTOs (responsabilidad de UserMapper)
     */
    private Set<Role> loadRoles(Set<UUID> roleIds) {
        Set<Role> roles = new HashSet<>();
        for (UUID roleId : roleIds) {
            rolePersistencePort.findById(roleId)
                .ifPresent(roles::add);
        }
        return roles;
    }
}
```

**¿Por qué aplica SRP?**

- ✅ **Una responsabilidad:** Gestionar lógica de usuarios
- ✅ **Delegación clara:** Persistencia, seguridad, mapeo están delegados
- ✅ **Una razón para cambiar:** Si cambian las reglas de negocio de usuarios
- ✅ **Fácil testear:** Mockear puertos es simple
- ✅ **Fácil mantener:** Cambios localizados

---

#### ✅ Ejemplo 2: Separación de Responsabilidades por Capas

**Ubicación:** [auth-service/adapter/in/web/AuthRestController.java](backend/auth-service/src/main/java/com/novacommerce/auth_service/adapter/in/web/AuthRestController.java)

```java
/**
 * RESPONSABILIDAD ÚNICA: Adaptar HTTP requests a use cases
 * NO hace: lógica de negocio, persistencia, validación compleja
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints de autenticación")
public class AuthRestController {
    
    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    
    /**
     * RESPONSABILIDAD: 
     * 1. Recibir HTTP request
     * 2. Validar estructura básica
     * 3. Delegar a use case
     * 4. Retornar HTTP response
     */
    @PostMapping("/login")
    @Operation(summary = "Autenticar usuario")
    public ResponseEntity<LoginResponse> authenticate(
            @Valid @RequestBody LoginRequest loginRequest) {
        
        log.info("Login request para: {}", loginRequest.getUserIdentifier());
        
        // DELEGACIÓN a la aplicación
        LoginResponse response = authenticateUserUseCase.authenticate(loginRequest);
        
        log.info("Login exitoso para: {}", loginRequest.getUserIdentifier());
        return ResponseEntity.ok(response);
    }
}
```

**Comparación de Responsabilidades:**

| Componente | Responsabilidad | SRP ✓ |
|------------|-----------------|-------|
| **AuthRestController** | Adaptar HTTP → use case | ✅ Una sola |
| **AuthService** | Lógica de autenticación | ✅ Una sola |
| **JwtTokenAdapter** | Generar JWT | ✅ Una sola |
| **UserServiceAdapter** | Comunicarse con user-service | ✅ Una sola |

Si cambiasen los requisitos de **autenticación**, solo cambiaría `AuthService`.
Si cambiasen los requisitos de **formato HTTP**, solo cambiaría `AuthRestController`.

---

### 2️⃣ O - Open/Closed Principle (OCP)

**Definición:** Las clases deben estar abiertas para extensión pero cerradas para modificación.

#### ✅ Ejemplo 1: Strategy Pattern - Descuentos Extensibles

**Ubicación:** [order-service/domain/discount/DiscountStrategy.java](backend/order-service/src/main/java/com/novacommerce/order_service/domain/discount/DiscountStrategy.java)

```java
/**
 * ABIERTO para EXTENSIÓN: nuevas estrategias implementan esta interfaz
 * CERRADO para MODIFICACIÓN: no cambiar DiscountStrategy al agregar nuevas estrategias
 */
public interface DiscountStrategy {
    
    /**
     * Aplica la estrategia de descuento
     */
    DiscountResult apply(DiscountContext context);
    
    String getName();
    
    boolean isApplicable(DiscountContext context);
}
```

**Implementaciones Existentes:**

```java
// ✅ CERRADO: DiscountStrategy no cambia
// ✅ ABIERTO: Nuevas estrategias sin tocar la interfaz

@Component
public class LoyaltyDiscountStrategy implements DiscountStrategy {
    @Override
    public DiscountResult apply(DiscountContext context) {
        // Implementación específica de lealtad
    }
}

// Fácil agregar nuevas sin cambiar código existente:
@Component
public class SeasonalDiscountStrategy implements DiscountStrategy {
    @Override
    public DiscountResult apply(DiscountContext context) {
        // Implementación específica de temporada
    }
}

@Component
public class BulkDiscountStrategy implements DiscountStrategy {
    @Override
    public DiscountResult apply(DiscountContext context) {
        // Implementación específica de volumen
    }
}
```

**Uso OCP en OrderService:**

```java
@Service
public class OrderService implements CreateOrderUseCase {
    
    private final List<DiscountStrategy> discountStrategies;
    
    /**
     * ABIERTO: Soporta cualquier cantidad de estrategias nuevas
     * CERRADO: NO necesita cambios cuando se agregan nuevas estrategias
     */
    public void applyDiscounts(Order order, DiscountContext context) {
        discountStrategies.stream()
            .filter(strategy -> strategy.isApplicable(context))
            .forEach(strategy -> {
                DiscountResult result = strategy.apply(context);
                order.addDiscount(result);
            });
    }
}
```

**¿Por qué OCP?**

- ✅ **Abierto:** Agregar `VIPDiscountStrategy` sin tocar código existente
- ✅ **Cerrado:** `DiscountStrategy` nunca cambia
- ✅ **Beneficio:** Nuevas funcionalidades sin bugs en lo existente

---

#### ✅ Ejemplo 2: Ports Pattern - Extensibilidad de Adaptadores

**Ubicación:** [nova-gateway/application/port/TokenValidatorPort.java](backend/nova-gateway/src/main/java/com/novacommerce/gateway/application/port/TokenValidatorPort.java)

```java
/**
 * ABIERTO para extensión: implementar con diferentes librerías JWT
 * CERRADO para modificación: cambiar a Nimbus JWT sin afectar gateway
 */
public interface TokenValidatorPort {
    boolean validateToken(String token);
    String extractUsername(String token);
    String extractAuthorities(String token);
    Token extractToken(String token);
}
```

**Implementación Actual (JJWT):**

```java
@Component
public class JwtTokenValidatorAdapter implements TokenValidatorPort {
    
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    
    private Key key;
    
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
    
    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

**Alternativa Futura (Nimbus JWT) - SIN CAMBIAR GATEWAY:**

```java
// Nueva implementación, mismo contrato
@Component("nimbusTokenValidator")
public class NimbusJwtTokenValidatorAdapter implements TokenValidatorPort {
    
    @Override
    public boolean validateToken(String token) {
        // Implementación con Nimbus JWT
        // ...
    }
}

// En application.yaml
spring:
  main:
    web-application-type: reactive

// El gateway sigue funcionando sin cambios
```

**Ventaja:** El gateway **jamás cambia** aunque cambies la librería JWT.

---

### 3️⃣ L - Liskov Substitution Principle (LSP)

**Definición:** Los objetos de una subclase deben poder reemplazar a los de la superclase sin romper el código.

#### ✅ Ejemplo: Exception Hierarchy

**Ubicación:** [auth-service/web/rest/exceptions/AuthServiceException.java](backend/auth-service/src/main/java/com/novacommerce/auth_service/web/rest/exceptions/AuthServiceException.java)

```java
/**
 * Clase base para excepciones del servicio
 */
public abstract class AuthServiceException extends RuntimeException {
    public AuthServiceException(String message) {
        super(message);
    }
}

// Subclases
public class DuplicateResourceException extends AuthServiceException {
    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s con %s = %s ya existe", resourceName, fieldName, fieldValue));
    }
}

public class InvalidCredentialsException extends AuthServiceException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}

public class InvalidTokenException extends AuthServiceException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
```

**LSP en GlobalExceptionHandler:**

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * LSP: Cualquier subclase de AuthServiceException 
     * se comporta correctamente aquí
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(
            DuplicateResourceException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex, request);
    }
    
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex, request);
    }
    
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(
            InvalidTokenException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex, request);
    }
    
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status, AuthServiceException ex, WebRequest request) {
        // LSP: Todas las excepciones responden igual a getMessage()
        ErrorResponse response = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .message(ex.getMessage())
            .path(extractPath(request))
            .build();
        
        return ResponseEntity.status(status).body(response);
    }
}
```

**¿Por qué LSP?**

- ✅ **Sustitución:** `DuplicateResourceException` reemplaza `AuthServiceException`
- ✅ **Contrato:** Todas comparten `getMessage()` 
- ✅ **Consistencia:** El handler funciona para cualquier subclase
- ❌ **No viola LSP:** Las subclases nunca rompen el contrato

---

### 4️⃣ I - Interface Segregation Principle (ISP)

**Definición:** Los clientes no deben depender de interfaces que no usan. Mejor muchas interfaces específicas que una genérica.

#### ✅ Ejemplo: Puertos Específicos en User-Service

**Ubicación:** [user-service/application/port/in/](backend/user-service/src/main/java/com/novacommerce/user_service/application/port/in/)

```java
/**
 * INTERFAZ ESPECÍFICA 1: Para gestión de usuarios
 * Un cliente que solo necesita crear usuarios, usa esto
 */
public interface ManageUsersUseCase {
    User createUser(User user, Set<UUID> roleIds);
    User updateUser(UUID userId, User userData);
    void deleteUser(UUID userId);
}

/**
 * INTERFAZ ESPECÍFICA 2: Para validación de credenciales
 * Un cliente (auth-service) que solo necesita validar, usa esto
 * No sabe de createUser, updateUser, deleteUser
 */
public interface ValidateUserCredentialsUseCase {
    InternalUserValidationResponse validateCredentials(String userIdentifier, String password);
}

/**
 * INTERFAZ ESPECÍFICA 3: Para gestión de roles
 * Clientes que solo gestionen roles, usan esto
 */
public interface ManageRolesUseCase {
    RoleResponse createRole(CreateRoleRequest request);
    RoleResponse updateRole(UUID roleId, CreateRoleRequest request);
    void deleteRole(UUID roleId);
}
```

**Implementación ISP:**

```java
@Service
public class UserService implements ManageUsersUseCase, ValidateUserCredentialsUseCase {
    // Solo implementa lo que necesita
}

@Service
public class RoleService implements ManageRolesUseCase {
    // Solo implementa gestión de roles
}
```

**¿Por qué ISP?**

- ✅ **Segregación:** `auth-service` solo ve `ValidateUserCredentialsUseCase`
- ✅ **Mínimas dependencias:** auth-service no sabe de createUser, updateUser
- ✅ **Bajo acoplamiento:** Cambios en ManageUsersUseCase no afectan auth-service
- ✅ **Testabilidad:** Mock de ValidateUserCredentialsUseCase es simple

---

### 5️⃣ D - Dependency Inversion Principle (DIP)

**Definición:** Depender de abstracciones, no de implementaciones concretas.

#### ✅ Ejemplo: Inyección de Dependencias vía Puertos

**Ubicación:** [auth-service/application/service/AuthService.java](backend/auth-service/src/main/java/com/novacommerce/auth_service/application/service/AuthService.java)

```java
/**
 * DIP: AuthService depende de ABSTRACCIONES (puertos), no de implementaciones
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService implements AuthenticateUserUseCase, RefreshTokenUseCase {
    
    // PUERTOS (abstracciones) - NO implementaciones concretas
    private final TokenGeneratorPort tokenGeneratorPort;
    private final UserValidationPort userValidationPort;
    
    /**
     * @param tokenGeneratorPort: Abstracción de generación de tokens
     *                            (podría ser JwtTokenAdapter, KeycloakAdapter, etc.)
     * @param userValidationPort: Abstracción de validación de usuarios
     *                            (podría ser UserServiceAdapter, DatabaseAdapter, etc.)
     */
    @Override
    public LoginResponse authenticate(LoginRequest loginRequest) {
        // Delegar a puertos (abstracciones)
        UserValidationResponse userResponse = userValidationPort.validateCredentials(
            new UserValidationRequest(
                loginRequest.getUserIdentifier(),
                loginRequest.getPassword()
            )
        );
        
        if (!userResponse.isEnabled()) {
            throw new InvalidCredentialsException("Usuario deshabilitado");
        }
        
        // Generar token (abstracción)
        String accessToken = tokenGeneratorPort.generateToken(
            userResponse.getUsername(),
            userResponse.getPermissions(),
            userResponse.getCustomerId()
        );
        
        String refreshToken = tokenGeneratorPort.generateRefreshToken(
            userResponse.getUsername()
        );
        
        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(3600L)
            .build();
    }
}
```

**Abstracción vs Concreción:**

```
ANTES (SIN DIP - INCORRECTO):
AuthService → JwtTokenAdapter (CONCRETO - ACOPLADO)
           → UserServiceFeignClient (CONCRETO - ACOPLADO)

PROBLEMA: Cambiar de JWT a OAuth rompe AuthService

DESPUÉS (CON DIP - CORRECTO):
AuthService → TokenGeneratorPort (ABSTRACCIÓN)
           → UserValidationPort (ABSTRACCIÓN)
              ↓
              JwtTokenAdapter (una implementación de TokenGeneratorPort)
              KeycloakAdapter (otra implementación posible)

VENTAJA: Cambiar de JWT a OAuth, solo cambias la implementación
```

**¿Por qué DIP?**

- ✅ **Inversión:** AuthService no conoce JwtTokenAdapter
- ✅ **Flexibilidad:** Cambiar JWT sin tocar AuthService
- ✅ **Testabilidad:** Mockear puertos es fácil
- ✅ **Escalabilidad:** Agregar nuevas implementaciones sin cambios

---

## Patrones de Diseño

Nova Commerce implementa varios patrones que mejoran su arquitectura.

### 1. Strategy Pattern (Descuentos)

**Ubicación:** [order-service/domain/discount/](backend/order-service/src/main/java/com/novacommerce/order_service/domain/discount/)

```java
// Interfaz Strategy
public interface DiscountStrategy {
    DiscountResult apply(DiscountContext context);
    boolean isApplicable(DiscountContext context);
}

// Múltiples implementaciones
@Component
public class LoyaltyDiscountStrategy implements DiscountStrategy { ... }

@Component
public class ProductTypeDiscountStrategy implements DiscountStrategy { ... }

// Uso en OrderService
List<DiscountStrategy> strategies; // Inyectado
strategies.stream()
    .filter(s -> s.isApplicable(context))
    .forEach(s -> order.addDiscount(s.apply(context)));
```

**Beneficios:**
- ✅ Nuevos descuentos sin modificar OrderService
- ✅ Cada estrategia independiente y testeable
- ✅ Composición de descuentos

---

### 2. Repository Pattern (Persistencia)

**Ubicación:** [user-service/repository/](backend/user-service/src/main/java/com/novacommerce/user_service/repository/)

```java
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
}
```

**Beneficios:**
- ✅ Abstracción de BD (cambiar BD es fácil)
- ✅ Spring Data JPA genera implementación automática
- ✅ Consultas específicas del dominio

---

### 3. Port & Adapter Pattern (Hexagonal Architecture)

**Ubicación:** Toda la aplicación

```
Dominio (NO conoce nada externo)
   ↑
Aplicación (Define puertos)
   ↑
Adaptadores (Implementan puertos)
   - REST Controllers (Entrada)
   - Persistence Adapters (Salida)
   - JWT Adapters (Salida)
   - Feign Clients (Salida)
```

**Beneficios:**
- ✅ Cambiar frameworks sin tocar lógica
- ✅ Testing sin BD, sin HTTP, sin JWT
- ✅ Independencia de detalles técnicos

---

### 4. Factory Pattern (Money.of())

**Ubicación:** [order-service/domain/model/Money.java](backend/order-service/src/main/java/com/novacommerce/order_service/domain/model/Money.java)

```java
@Value
public class Money {
    BigDecimal amount;
    
    private Money(BigDecimal amount) {
        // Validación
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid amount");
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }
    
    // Factory methods
    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }
    
    public static Money of(double amount) {
        return new Money(BigDecimal.valueOf(amount));
    }
    
    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }
}
```

**Beneficios:**
- ✅ Validación garantizada
- ✅ Control total de creación
- ✅ Métodos semánticos (zero, of)

---

### 5. Mapper Pattern (MapStruct)

**Ubicación:** [product-service/adapter/in/web/mapper/](backend/product-service/src/main/java/com/novacommerce/product_service/adapter/in/web/mapper/)

```java
@Mapper(componentModel = "spring")
public interface ProductDtoMapper {
    
    // Request → Domain
    Product toDomain(ProductRequest request);
    
    // Domain → Response
    ProductResponse toResponse(Product domain);
    
    // Collections
    List<ProductResponse> toResponseList(List<Product> products);
}
```

**Beneficios:**
- ✅ Conversión automática (compilación)
- ✅ Separación DTO ↔ Domain
- ✅ Null-safe

---

## Clean Code

Principios de Clean Code aplicados en Nova Commerce.

### 1. Semántica: Nombres Descriptivos

#### ✅ Buena Semántica - Auth Service

```java
// Métodos con nombres claros
public interface AuthenticateUserUseCase {
    LoginResponse authenticate(LoginRequest loginRequest);
}

public interface RefreshTokenUseCase {
    LoginResponse refreshToken(String refreshToken);
}

public interface ValidateTokenUseCase {
    TokenValidationResponse validateToken(String token);
}
```

**¿Por qué es clara?**

- ✅ `authenticate()` - Qué hace: autentica usuario
- ✅ `refreshToken()` - Qué hace: refresca token
- ✅ `validateToken()` - Qué hace: valida token
- ❌ NO usar: `process()`, `handle()`, `execute()`

---

#### ✅ Variables Descriptivas

```java
// ❌ MAL - Sin contexto
public LoginResponse authenticate(LoginRequest req) {
    String tk = generateToken(req.getUser(), req.getPwd());
    return new LoginResponse(tk);
}

// ✅ BIEN - Descriptivas
public LoginResponse authenticate(LoginRequest loginRequest) {
    String accessToken = tokenGeneratorPort.generateToken(
        loginRequest.getUserIdentifier(),
        loginRequest.getPassword()
    );
    return LoginResponse.bearer(accessToken, refreshToken, 3600L);
}
```

---

### 2. DRY - Don't Repeat Yourself

#### ✅ Extracción de Lógica Común

**Ubicación:** [order-service/domain/model/Money.java](backend/order-service/src/main/java/com/novacommerce/order_service/domain/model/Money.java)

```java
@Value
public class Money {
    
    // ❌ REPETICIÓN - si Money.of() no existiera:
    // Order.java: Money total = new Money(BigDecimal.valueOf(100.00));
    // OrderItem.java: Money price = new Money(BigDecimal.valueOf(50.00));
    // Discount.java: Money amount = new Money(BigDecimal.valueOf(10.00));
    
    // ✅ DRY - Factory centraliza lógica
    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }
    
    public static Money of(double amount) {
        return new Money(BigDecimal.valueOf(amount));
    }
}

// Uso consistente
Money total = Money.of(100.00);        // Una línea, consistente
Money price = Money.of(50.00);         // Una línea, consistente
Money discount = Money.of(10.00);      // Una línea, consistente
```

---

#### ✅ Eliminación de Duplicación en Servicios

**Ubicación:** [user-service/config/DataInitializer.java](backend/user-service/src/main/java/com/novacommerce/user_service/config/DataInitializer.java)

```java
@Component
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true")
public class DataInitializer implements ApplicationRunner {
    
    // ❌ DUPLICACIÓN SIN MÉTODO:
    // if (roleRepository.findByName("ADMIN").isEmpty()) {
    //     roleRepository.save(adminRole);
    // }
    // if (roleRepository.findByName("USER").isEmpty()) {
    //     roleRepository.save(userRole);
    // }
    // if (roleRepository.findByName("GUEST").isEmpty()) {
    //     roleRepository.save(guestRole);
    // }
    
    // ✅ DRY - Método reutilizable
    private void createRoleIfNotExists(String roleName, Set<Permission> permissions) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = Role.builder()
                .name(roleName)
                .permissions(permissions)
                .build();
            roleRepository.save(role);
            log.info("Rol {} creado", roleName);
        }
    }
    
    // Uso
    createRoleIfNotExists("ADMIN", adminPermissions);
    createRoleIfNotExists("USER", userPermissions);
    createRoleIfNotExists("GUEST", guestPermissions);
}
```

---

#### ✅ Extracción de Métodos Privados

**Ubicación:** [auth-service/config/security/jwt/JwtAuthenticationFilter.java](backend/auth-service/src/main/java/com/novacommerce/auth_service/config/security/jwt/JwtAuthenticationFilter.java)

```java
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider tokenProvider;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        try {
            // ✅ Extracción de responsabilidades
            String token = extractTokenFromRequest(request);
            
            if (token != null && tokenProvider.validateToken(token)) {
                String username = tokenProvider.extractUsername(token);
                String authorities = tokenProvider.extractAuthorities(token);
                
                Collection<SimpleGrantedAuthority> grantedAuthorities = 
                    parseAuthorities(authorities);
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.error("Error en autenticación", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    // ✅ Método privado reutilizable
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authorizationHeader) && 
            authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
    
    // ✅ Método privado reutilizable
    private Collection<SimpleGrantedAuthority> parseAuthorities(String authorities) {
        if (!StringUtils.hasText(authorities)) {
            return Collections.emptyList();
        }
        return Arrays.stream(authorities.split(","))
            .map(String::trim)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }
}
```

---

### 3. Complejidad: Código Legible

#### ✅ Métodos Cortos y Claros

```java
// ❌ COMPLEJO - Demasiada lógica en un método
public void processOrder(Order order) {
    if (order != null && order.getItems() != null) {
        if (order.getItems().size() > 0) {
            BigDecimal total = BigDecimal.ZERO;
            for (OrderItem item : order.getItems()) {
                if (item.getPrice() != null) {
                    total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    if (item.getPrice().compareTo(BigDecimal.valueOf(100)) > 0) {
                        // descuentos...
                    }
                }
            }
            order.setTotal(Money.of(total));
        }
    }
}

// ✅ CLARO - Métodos pequeños y composables
public void processOrder(Order order) {
    validateOrder(order);
    calculateOrderTotal(order);
    applyDiscounts(order);
    persistOrder(order);
}

private void validateOrder(Order order) {
    if (order == null || order.getItems().isEmpty()) {
        throw new InvalidOrderException("Order must have items");
    }
}

private void calculateOrderTotal(Order order) {
    Money total = order.getItems().stream()
        .map(item -> calculateItemTotal(item))
        .reduce(Money.zero(), Money::add);
    
    order.setTotal(total);
}

private Money calculateItemTotal(OrderItem item) {
    return item.getPrice()
        .multiply(item.getQuantity());
}

private void applyDiscounts(Order order) {
    discountStrategies.stream()
        .filter(s -> s.isApplicable(context))
        .forEach(s -> order.addDiscount(s.apply(context)));
}
```

**Beneficios:**
- ✅ Cada método hace UNA cosa
- ✅ Fácil de leer top-down
- ✅ Fácil de testear

---

#### ✅ Evitar Anidamientos Excesivos

```java
// ❌ ANIDADO - Difícil de leer
public void validateUser(User user) {
    if (user != null) {
        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            if (user.getEmail() != null && user.getEmail().contains("@")) {
                if (user.getPassword() != null && user.getPassword().length() >= 8) {
                    // Procesar...
                } else {
                    throw new InvalidPasswordException("Password too short");
                }
            } else {
                throw new InvalidEmailException("Invalid email format");
            }
        } else {
            throw new InvalidUsernameException("Username required");
        }
    } else {
        throw new InvalidUserException("User is null");
    }
}

// ✅ GUARDIAS - Legible y claro
public void validateUser(User user) {
    if (user == null) {
        throw new InvalidUserException("User is null");
    }
    if (user.getUsername() == null || user.getUsername().isEmpty()) {
        throw new InvalidUsernameException("Username required");
    }
    if (user.getEmail() == null || !user.getEmail().contains("@")) {
        throw new InvalidEmailException("Invalid email format");
    }
    if (user.getPassword() == null || user.getPassword().length() < 8) {
        throw new InvalidPasswordException("Password too short");
    }
    // Procesar...
}
```

**Beneficio:** Guardia temprana → Menos indentación

---

#### ✅ Usar Métodos de Utilidad para Lógica Común

**Ubicación:** [order-service/domain/model/Order.java](backend/order-service/src/main/java/com/novacommerce/order_service/domain/model/Order.java)

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    private Long id;
    private Long customerId;
    private List<OrderItem> items;
    private Money totalBeforeDiscount;
    private Money discountTotal;
    private Money totalAfterDiscount;
    private OrderStatus status;
    
    // ✅ Métodos de negocio claros
    public void addItem(OrderItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
    }
    
    public void recalculateTotals() {
        this.totalBeforeDiscount = calculateSubtotal();
        // discountTotal se calcula con estrategias
        this.totalAfterDiscount = 
            totalBeforeDiscount.subtract(discountTotal);
    }
    
    public void applyDiscount(Money discountAmount) {
        if (discountAmount.isGreaterThan(totalBeforeDiscount)) {
            throw new IllegalArgumentException("Discount > total");
        }
        this.discountTotal = discountAmount;
        this.totalAfterDiscount = 
            totalBeforeDiscount.subtract(discountAmount);
    }
    
    public void changeStatus(OrderStatus newStatus) {
        validateStatusTransition(this.status, newStatus);
        this.status = newStatus;
    }
    
    // ✅ Métodos privados que simplifican el flujo
    private Money calculateSubtotal() {
        return items.stream()
            .map(item -> item.getPrice().multiply(item.getQuantity()))
            .reduce(Money.zero(), Money::add);
    }
    
    private void validateStatusTransition(OrderStatus from, OrderStatus to) {
        if (from == OrderStatus.COMPLETED && to != OrderStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot change completed order");
        }
    }
}
```

---

## Conclusiones

### ✅ Nova Commerce Implementa Correctamente:

| Principio | Implementación | Beneficio |
|-----------|----------------|-----------|
| **SRP** | Servicios con una responsabilidad | Fácil mantener y testear |
| **OCP** | Strategy Pattern para descuentos | Extensible sin modificar |
| **LSP** | Exception Hierarchy | Sustitución segura |
| **ISP** | Puertos específicos | Bajo acoplamiento |
| **DIP** | Inyección de puertos | Flexible e independiente |
| **Strategy** | Descuentos polimórficos | Composición dinámnica |
| **Repository** | Abstracción de persistencia | Cambiar BD es fácil |
| **Port & Adapter** | Hexagonal Architecture | Testeable sin frameworks |
| **Factory** | Money.of() | Validación garantizada |
| **Mapper** | MapStruct | Conversión automática |

### 🎯 Resultado Final:

- ✅ **Código limpio y legible**
- ✅ **Altamente mantenible**
- ✅ **Fácil de testear**
- ✅ **Extensible sin modificar**
- ✅ **Independencia de frameworks**
- ✅ **Escalable a múltiples microservicios**

Nova Commerce es un **ejemplo de arquitectura profesional** lista para producción.
