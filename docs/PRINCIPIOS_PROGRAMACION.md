# 📚 Principios de Programación en Nova Commerce

## Introducción

Este documento mapea los **4 principios clave de programación** evaluados en la imagen proporcionada y demuestra dónde se aplican en el código de **Nova Commerce**.

La arquitectura de Nova Commerce implementa **Hexagonal Architecture (Ports & Adapters) + Clean Architecture**, lo que facilita la aplicación correcta de estos principios.

---

## 1️⃣ POO: Abstracción y Encapsulamiento

### Definición
La **Abstracción** oculta la complejidad mediante interfaces que definen QUÉ se debe hacer sin mostrar CÓMO.
El **Encapsulamiento** protege los datos internos y los expone solo a través de una interfaz controlada.

### ✅ Ejemplo 1: TokenValidatorPort (Interfaz como Abstracción)

**Ubicación:** [nova-gateway/application/port/TokenValidatorPort.java](nova-gateway/src/main/java/com/novacommerce/gateway/application/port/TokenValidatorPort.java)

```java
/**
 * Puerto (interfaz) para validar tokens JWT
 * Define el contrato que deben implementar los adaptadores de validación de tokens
 */
public interface TokenValidatorPort {

    /**
     * Valida un token JWT
     * @param token el token a validar
     * @return true si el token es válido, false en caso contrario
     */
    boolean validateToken(String token);

    /**
     * Extrae el nombre de usuario del token
     * @param token el token JWT
     * @return el nombre de usuario
     */
    String extractUsername(String token);

    /**
     * Extrae las autoridades del token como una cadena separada por comas
     * @param token el token JWT
     * @return las autoridades separadas por comas
     */
    String extractAuthorities(String token);

    /**
     * Extrae el modelo de Token con toda la información
     * @param token el token JWT
     * @return el modelo Token con información validada
     */
    Token extractToken(String token);
}
```

**¿Por qué aplica?**

- ✅ **Abstracción:** La interfaz define un contrato (QUÉ) sin revelar cómo se valida el JWT
- ✅ **Encapsulamiento:** Los detalles de validación (JJWT, HS512, etc.) están ocultos en la implementación
- ✅ **Desacoplamiento:** El gateway no depende de la librería específica de JWT, solo de esta interfaz
- ✅ **Extensibilidad:** Se podría cambiar de JJWT a Nimbus o cualquier otra librería sin afectar el gateway

**Implementación:** [auth-service/adapter/out/jwt/JwtTokenAdapter.java](auth-service/src/main/java/com/novacommerce/auth_service/adapter/out/jwt/JwtTokenAdapter.java)

```java
@Component
public class JwtTokenAdapter implements TokenValidatorPort {
    
    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public Token extractToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
        
        // Extrae información del JWT
        return Token.builder()
            .value(token)
            .username(claims.getSubject())
            .authorities(parseAuthorities(claims))
            .expirationTime(claims.getExpiration().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime())
            .valid(true)
            .build();
    }
}
```

---

### ✅ Ejemplo 2: JwtAuthenticationFilter (Encapsulamiento de Lógica)

**Ubicación:** [auth-service/config/security/jwt/JwtAuthenticationFilter.java](auth-service/src/main/java/com/novacommerce/auth_service/config/security/jwt/JwtAuthenticationFilter.java)

```java
/**
 * Filtro de autenticación JWT.
 * Intercepta cada request, extrae el token JWT del header Authorization,
 * lo valida y establece el contexto de seguridad.
 * Se ejecuta una sola vez por request.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        try {
            String token = extractTokenFromRequest(request);
            
            if (token != null && tokenProvider.validateToken(token)) {
                String username = tokenProvider.extractUsername(token);
                String authorities = tokenProvider.extractAuthorities(token);
                
                Collection<SimpleGrantedAuthority> grantedAuthorities = 
                    parseAuthorities(authorities);
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
                
                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.error("No se pudo establecer la autenticación", ex);
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del header Authorization del request.
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(JwtConstants.AUTHORIZATION_HEADER);
        return tokenProvider.extractTokenFromHeader(authorizationHeader);
    }

    /**
     * Parsea las autoridades desde una cadena separada por comas.
     */
    private Collection<SimpleGrantedAuthority> parseAuthorities(String authorities) {
        if (!StringUtils.hasText(authorities)) {
            return java.util.Collections.emptyList();
        }
        return Arrays.stream(authorities.split(","))
            .map(String::trim)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }
}
```

**¿Por qué aplica?**

- ✅ **Encapsulamiento:** La lógica del filtro está encapsulada en métodos privados
- ✅ **Responsabilidad única:** El filtro solo valida tokens, no crea usuarios
- ✅ **Ocultamiento de información:** Los detalles de parseo de autoridades están en método privado

---

## 2️⃣ POO: Herencia y Polimorfismo

### Definición
La **Herencia** permite que una clase herede comportamiento de otra.
El **Polimorfismo** permite que objetos diferentes implementen la misma interfaz de formas distintas.

### ✅ Ejemplo 1: Strategy Pattern - Descuentos Polimórficos

**Interfaz Base:** [order-service/domain/discount/DiscountStrategy.java](order-service/src/main/java/com/novacommerce/order_service/domain/discount/DiscountStrategy.java)

```java
/**
 * Interfaz Strategy para descuentos.
 * Cada implementación define una estrategia diferente de descuento.
 */
public interface DiscountStrategy {
    
    /**
     * Aplica la estrategia de descuento al contexto dado.
     * 
     * @param context contexto con información de cliente, orden e items
     * @return resultado del descuento aplicado
     */
    DiscountResult apply(DiscountContext context);
    
    /**
     * Nombre de la estrategia para logging y debugging.
     */
    String getName();
    
    /**
     * Verifica si esta estrategia es aplicable al contexto dado.
     */
    boolean isApplicable(DiscountContext context);
}
```

**Implementación 1: Descuento por Lealtad**

**Ubicación:** [order-service/domain/discount/LoyaltyDiscountStrategy.java](order-service/src/main/java/com/novacommerce/order_service/domain/discount/LoyaltyDiscountStrategy.java)

```java
/**
 * Estrategia de descuento basada en nivel de fidelidad del cliente.
 * 
 * Reglas:
 * - BRONZE: 5%
 * - SILVER: 10%
 * - GOLD: 15%
 * - VIP: 20%
 */
@Component
public class LoyaltyDiscountStrategy implements DiscountStrategy {
    
    @Override
    public DiscountResult apply(DiscountContext context) {
        BigDecimal percentage = getDiscountPercentage(context.getLoyaltyLevel());
        BigDecimal discount = context.getTotalAmount()
            .multiply(percentage);
        
        return DiscountResult.builder()
            .discountAmount(discount)
            .strategyName(getName())
            .reason("Descuento por nivel de lealtad: " + context.getLoyaltyLevel())
            .build();
    }
    
    @Override
    public String getName() {
        return "LOYALTY_DISCOUNT";
    }
    
    @Override
    public boolean isApplicable(DiscountContext context) {
        return context.hasLoyaltyLevel() && context.isCustomerActive();
    }
    
    private BigDecimal getDiscountPercentage(String loyaltyLevel) {
        return switch(loyaltyLevel.toUpperCase()) {
            case "BRONZE" -> BigDecimal.valueOf(0.05);
            case "SILVER" -> BigDecimal.valueOf(0.10);
            case "GOLD" -> BigDecimal.valueOf(0.15);
            case "VIP" -> BigDecimal.valueOf(0.20);
            default -> BigDecimal.ZERO;
        };
    }
}
```

**Uso Polimórfico:** [order-service/application/service/OrderService.java](order-service/src/main/java/com/novacommerce/order_service/application/service/OrderService.java)

```java
@Service
public class OrderService {
    
    private final List<DiscountStrategy> discountStrategies;
    
    public void applyDiscounts(Order order, DiscountContext context) {
        // POLIMORFISMO: itera sobre diferentes estrategias
        // Sin saber exactamente cuál es, llama apply() en cada una
        discountStrategies.stream()
            .filter(strategy -> strategy.isApplicable(context))
            .forEach(strategy -> {
                DiscountResult result = strategy.apply(context);
                order.addDiscount(result);
            });
    }
}
```

**¿Por qué aplica?**

- ✅ **Polimorfismo:** Diferentes descuentos implementan `DiscountStrategy` de formas distintas
- ✅ **Extensibilidad:** Agregar nuevos descuentos sin modificar `OrderService`
- ✅ **Strategy Pattern:** El cliente no sabe qué descuento específico se aplica

**Otras implementaciones posibles:**
- `CouponDiscountStrategy` - Descuento por cupón
- `SeasonalDiscountStrategy` - Descuento por temporada
- `BulkDiscountStrategy` - Descuento por volumen

---

### ✅ Ejemplo 2: Excepción Hierarchy (Herencia)

**Clase Base Abstracta:** [auth-service/web/rest/exceptions/AuthServiceException.java](auth-service/src/main/java/com/novacommerce/auth_service/web/rest/exceptions/AuthServiceException.java)

```java
/**
 * Excepción base para errores de negocio del auth-service.
 */
public abstract class AuthServiceException extends RuntimeException {

    public AuthServiceException(String message) {
        super(message);
    }

    public AuthServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

**Subclases Concretas:** 

```java
// Excepción de recurso duplicado
public class DuplicateResourceException extends AuthServiceException {
    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s con %s = %s ya existe", resourceName, fieldName, fieldValue));
    }
}

// Excepción de credenciales inválidas
public class InvalidCredentialsException extends AuthServiceException {
    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException() {
        super("Credenciales inválidas");
    }
}

// Excepción de token inválido
public class InvalidTokenException extends AuthServiceException {
    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException() {
        super("Token inválido o expirado");
    }
}
```

**Manejo Polimórfico en GlobalExceptionHandler:**

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de recurso duplicado.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(
        DuplicateResourceException ex,
        WebRequest request) {
        log.warn("Recurso duplicado: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.CONFLICT.value())
            .error("Conflict")
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Maneja excepciones de credenciales inválidas.
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(
        InvalidCredentialsException ex,
        WebRequest request) {
        log.error("Credenciales inválidas: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.UNAUTHORIZED.value())
            .error("Unauthorized")
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
}
```

**¿Por qué aplica?**

- ✅ **Herencia:** Todas las excepciones heredan de `AuthServiceException`
- ✅ **Polimorfismo:** Se capturan excepciones específicas mediante `@ExceptionHandler`
- ✅ **Mantenibilidad:** Fácil agregar nuevas excepciones sin modificar el handler

---

## 3️⃣ Funcional: Inmutabilidad

### Definición
La **Inmutabilidad** significa que un objeto no puede cambiar después de su creación.
Esto previene efectos secundarios, simplifica debugging y hace el código thread-safe.

### ✅ Ejemplo 1: Value Object - Money (Inmutable)

**Ubicación:** [order-service/domain/model/Money.java](order-service/src/main/java/com/novacommerce/order_service/domain/model/Money.java)

```java
/**
 * Value Object para representar dinero con precisión y validaciones.
 * @Value de Lombok hace el objeto INMUTABLE:
 * - Todos los campos son final
 * - No hay setters
 * - getter automático
 * - equals, hashCode, toString automáticos
 */
@Value
public class Money {
    BigDecimal amount;

    // Constructor PRIVADO: control total sobre la creación
    private Money(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        // Validación: setScale a 2 decimales
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    // Factory methods: control total
    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    public static Money of(double amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    // Operaciones RETORNAN NUEVAS INSTANCIAS, no modifican la original
    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money subtract(Money other) {
        return new Money(this.amount.subtract(other.amount));
    }

    public Money multiply(int quantity) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)));
    }

    public Money multiply(BigDecimal multiplier) {
        return new Money(this.amount.multiply(multiplier));
    }

    // Operaciones de consulta (no modifican estado)
    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isGreaterThan(Money other) {
        return amount.compareTo(other.amount) > 0;
    }

    public boolean isLessThan(Money other) {
        return amount.compareTo(other.amount) < 0;
    }

    @Override
    public String toString() {
        return amount.toString();
    }
}
```

**Ejemplo de Uso:**

```java
// Crear dinero
Money originalPrice = Money.of(100.00);
Money discount = Money.of(10.00);

// Operaciones: NUNCA modifican originalPrice
Money finalPrice = originalPrice.subtract(discount);

// originalPrice sigue siendo 100.00
assertEquals("100.00", originalPrice.toString());
assertEquals("90.00", finalPrice.toString());

// Thread-safe: múltiples threads pueden leer originalPrice sin sincronización
```

**¿Por qué aplica?**

- ✅ **Inmutabilidad:** No hay setters, constructor privado
- ✅ **Seguridad:** Validaciones en el constructor garantizan estado válido
- ✅ **Thread-safety:** Sin sincronización necesaria para múltiples threads
- ✅ **Predecibilidad:** Sin efectos secundarios ocultos

---

### ✅ Ejemplo 2: DTOs Record (Inmutables)

**Ubicación:** [auth-service/web/api/dto/response/LoginResponse.java](auth-service/src/main/java/com/novacommerce/auth_service/web/api/dto/response/LoginResponse.java)

```java
/**
 * DTO de respuesta para login.
 * Record es INMUTABLE por defecto (Java 16+):
 * - Todos los campos son final
 * - No hay setters
 * - equals, hashCode, toString automáticos
 * - Constructor completo automático
 */
public record LoginResponse(
    @JsonProperty("access_token")
    String accessToken,

    @JsonProperty("refresh_token")
    String refreshToken,

    @JsonProperty("token_type")
    String tokenType,

    @JsonProperty("expires_in")
    Long expiresIn,

    @JsonProperty("username")
    String username,

    @JsonProperty("roles")
    List<String> roles
) {
    /**
     * Factory method para crear LoginResponse con tipo de token Bearer.
     */
    public static LoginResponse bearer(String accessToken, String refreshToken, Long expiresIn) {
        return new LoginResponse(accessToken, refreshToken, "Bearer", expiresIn, null, null);
    }
}
```

**Otros DTOs Inmutables:**

```java
// User Response DTO
public record UserResponse(
    UUID id,
    String username,
    String email,
    String status,
    Boolean enabled,
    Boolean locked,
    Long customerId,
    Set<RoleResponse> roles,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

// Role Response DTO
public record RoleResponse(
    UUID id,
    String name,
    String description,
    Set<PermissionResponse> permissions
) {}

// Login Request DTO
public record LoginRequest(
    @NotBlank(message = "El usuario o email es obligatorio")
    String userIdentifier,

    @NotBlank(message = "La contraseña es obligatoria")
    String password
) {}

// Token Validation Response
public record TokenValidationResponse(
    Boolean valid,
    String username,
    String authorities
) {
    public static TokenValidationResponse valid(String username, String authorities) {
        return new TokenValidationResponse(true, username, authorities);
    }

    public static TokenValidationResponse invalid() {
        return new TokenValidationResponse(false, null, null);
    }
}
```

**¿Por qué aplica?**

- ✅ **Inmutabilidad:** Records son inmutables por diseño
- ✅ **Brevedad:** Menos código boilerplate que clases tradicionales
- ✅ **Seguridad:** Los datos en tránsito no se pueden modificar accidentalmente
- ✅ **Thread-safety:** Múltiples threads pueden compartir la misma instancia

---

## 4️⃣ Funcional: Funciones Puras y High-Order Functions

### Definición
Una **Función Pura** no tiene efectos secundarios y siempre retorna el mismo resultado para los mismos inputs.
Las **High-Order Functions** son funciones que toman funciones como parámetros o las retornan.

### ✅ Ejemplo 1: Parseo de Autoridades (Función Pura con Streams)

**Ubicación:** [auth-service/config/security/jwt/JwtAuthenticationFilter.java](auth-service/src/main/java/com/novacommerce/auth_service/config/security/jwt/JwtAuthenticationFilter.java) (líneas 72-86)

```java
/**
 * Parsea las autoridades desde una cadena separada por comas.
 * FUNCIÓN PURA:
 * - No modifica estado externo
 * - Siempre retorna lo mismo para el mismo input
 * - Sin efectos secundarios
 */
private Collection<SimpleGrantedAuthority> parseAuthorities(String authorities) {
    if (!StringUtils.hasText(authorities)) {
        return java.util.Collections.emptyList();
    }
    
    // Stream API con operaciones PURAS
    return Arrays.stream(authorities.split(","))
        .map(String::trim)                           // Función pura: transforma cada elemento
        .map(SimpleGrantedAuthority::new)            // Función pura: crea nueva instancia
        .collect(Collectors.toList());               // Reducción: agrega en lista
}
```

**Análisis:**

| Operación | Tipo | Razón |
|-----------|------|-------|
| `.split(",")` | Base | Convierte string en array |
| `.stream()` | Transformación | Convierte en stream (iterable funcional) |
| `.map(String::trim)` | **Función pura** | Elimina espacios, sin modificar original |
| `.map(SimpleGrantedAuthority::new)` | **Función pura** | Crea objetos, sin efectos secundarios |
| `.collect(Collectors.toList())` | **Reducción** | Agrega resultados en una lista |

**Ejemplo de Uso:**

```java
// Input
String authorities = "ROLE_ADMIN, ROLE_USER, ROLE_DELETE";

// Ejecución pura: SIEMPRE retorna lo mismo
Collection<SimpleGrantedAuthority> result1 = parseAuthorities(authorities);
Collection<SimpleGrantedAuthority> result2 = parseAuthorities(authorities);

// result1 y result2 son idénticas
assertEquals(3, result1.size());
assertEquals(3, result2.size());

// Sin efectos secundarios: no modifica variables externas
```

---

### ✅ Ejemplo 2: Parseo de Autoridades Avanzado (High-Order Functions)

**Ubicación:** [user-service/adapter/in/filter/JwtAuthenticationFilter.java](user-service/src/main/java/com/novacommerce/user_service/adapter/in/filter/JwtAuthenticationFilter.java) (líneas 96-108)

```java
/**
 * Parsea las autoridades desde una cadena separada por comas.
 * Ejemplo de High-Order Functions con Stream API.
 */
private Set<SimpleGrantedAuthority> parseAuthorities(String authoritiesString) {
    if (authoritiesString == null || authoritiesString.isEmpty()) {
        return Collections.emptySet();
    }

    // COMPOSICIÓN de HIGH-ORDER FUNCTIONS
    return Stream.of(authoritiesString.split(","))
        .map(String::trim)                          // Función pura 1: trim
        .filter(s -> !s.isEmpty())                  // Predicado puro: filtrar vacíos
        .map(SimpleGrantedAuthority::new)           // Función pura 2: constructor
        .collect(Collectors.toSet());               // Reducción: colectar
}
```

**Análisis de High-Order Functions:**

```
Stream.of()                      ← Toma un array, retorna Stream (HIGH-ORDER)
    ├─ .map(String::trim)        ← HIGH-ORDER: aplica función a cada elemento
    ├─ .filter(predicate)        ← HIGH-ORDER: usa predicado (función) para filtrar
    ├─ .map(constructor)         ← HIGH-ORDER: aplica función (constructor)
    └─ .collect(collector)       ← HIGH-ORDER: agrega usando una función colectora
```

---

### ✅ Ejemplo 3: Mapper con High-Order Functions

**Ubicación:** [user-service/service/mapper/RoleMapperTest.java](user-service/src/test/java/com/novacommerce/user_service/service/mapper/RoleMapperTest.java)

```java
@Test
@DisplayName("Should map permissions correctly")
void testPermissionsMapping() {
    Role role = Role.builder()
        .id(UUID.randomUUID())
        .name("ADMIN")
        .permissions(Set.of(
            Permission.builder().id(UUID.randomUUID()).name("USER_READ").build(),
            Permission.builder().id(UUID.randomUUID()).name("USER_WRITE").build()
        ))
        .build();

    RoleResponse response = roleMapper.roleToRoleResponse(role);

    // HIGH-ORDER FUNCTION: Stream + filter + map + collect
    assertNotNull(response.permissions());
    assertEquals(1, response.permissions().size());
    
    // Uso de Stream con predicado PURO
    assertTrue(response.permissions().stream()
        .anyMatch(p -> p.name().equals("USER_READ")));
}
```

---

### ✅ Ejemplo 4: PublicProductService (Funciones Puras)

**Ubicación:** [product-service/application/service/PublicProductService.java](product-service/src/main/java/com/novacommerce/product_service/application/service/PublicProductService.java)

```java
@Service
@RequiredArgsConstructor
public class PublicProductService {
    
    private final ProductPersistencePort productPersistencePort;
    private final PublicProductDtoMapper mapper;

    /**
     * Obtiene productos aleatorios para la home page.
     * FUNCIÓN PURA:
     * - No modifica base de datos
     * - No tiene efectos secundarios
     * - Siempre retorna lo mismo para el mismo parámetro
     */
    public List<Product> getPublicHomeProducts(int limit) {
        // Función pura: consulta datos sin modificar
        return productPersistencePort.findActiveProductsWithStockRandomOrder(limit);
    }

    /**
     * Obtiene un producto público por ID.
     * FUNCIÓN PURA sin efectos secundarios.
     */
    public PublicProductResponse getProductById(Long id) {
        Product product = productPersistencePort.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        // Mapeo puro: transforma objeto sin efectos secundarios
        return mapper.toPublicResponse(product);
    }

    /**
     * Mapea lista de productos a DTOs con Stream (HIGH-ORDER FUNCTIONS).
     */
    public List<PublicProductResponse> getPublicHomeProductsList(int limit) {
        return getPublicHomeProducts(limit).stream()
            .map(mapper::toPublicResponse)           // HIGH-ORDER: aplica función a cada uno
            .collect(Collectors.toList());           // Reduce a lista
    }
}
```

**Test con Funciones Puras:**

```java
@Test
@DisplayName("Should respect limit parameter")
void testGetPublicHomeProductsWithDifferentLimit() {
    // Input definido
    List<Product> expectedProducts = Arrays.asList(product1);
    
    when(productPersistencePort.findActiveProductsWithStockRandomOrder(5))
        .thenReturn(expectedProducts);

    // Ejecución PURA: mismo input = mismo output
    List<Product> result = publicProductService.getPublicHomeProducts(5);

    // Verificación
    assertNotNull(result);
    assertEquals(1, result.size());
    verify(productPersistencePort, times(1))
        .findActiveProductsWithStockRandomOrder(5);
}
```

---

### ✅ Ejemplo 5: Transformación con Optional (HIGH-ORDER)

**Ubicación:** [customer-service/adapter/in/web/InternalCustomerController.java](customer-service/src/main/java/com/novacommerce/customer_service/adapter/in/web/InternalCustomerController.java)

```java
@GetMapping("/{id}")
public ResponseEntity<InternalCustomerResponse> getByIdInternal(@PathVariable Long id) {
    // HIGH-ORDER FUNCTION: map + orElseGet
    return manageCustomersUseCase.findById(id)
        .map(c -> ResponseEntity.ok(InternalCustomerResponse.from(c)))   // HIGH-ORDER
        .orElseGet(() -> ResponseEntity.notFound().build());             // HIGH-ORDER
}
```

**Análisis:**

```
Optional<Customer>.map()        ← HIGH-ORDER: aplica función si existe
Optional<Customer>.orElseGet()  ← HIGH-ORDER: retorna alternativa si vacío
```

---

## 📊 Tabla Comparativa: Dónde se Aplican los Principios

| Principio | Ubicación | Beneficio | Ejemplo |
|-----------|-----------|-----------|---------|
| **Abstracción** | TokenValidatorPort | Desacoplamiento | Cambiar JWT sin afectar gateway |
| **Encapsulamiento** | JwtAuthenticationFilter | Ocultamiento | parseAuthorities es privado |
| **Polimorfismo** | DiscountStrategy | Extensibilidad | Nuevos descuentos sin cambios |
| **Herencia** | AuthServiceException | Reutilización | Todas las excepciones heredan |
| **Inmutabilidad** | Money Value Object | Thread-safety | add() retorna nueva instancia |
| **Inmutabilidad** | LoginResponse Record | Brevedad | Sin setters automáticos |
| **Función Pura** | parseAuthorities | Testabilidad | Mismo input = mismo output |
| **High-Order** | Stream.map().filter() | Composición | Encadenar operaciones |

---

## 🎯 Cómo se Integran en la Arquitectura

```
┌─────────────────────────────────────────────────────────────────┐
│                     NOVA COMMERCE ARCHITECTURE                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐           │
│  │   Gateway    │  │    Auth-S    │  │   User-S     │           │
│  │  (Puertos)   │  │  (Adapters)  │  │  (Services)  │           │
│  └──────────────┘  └──────────────┘  └──────────────┘           │
│        │                  │                  │                   │
│   Abstracción        Encapsulamiento    Polimorfismo            │
│   (Interfaces)       (Privados)         (Excepciones)           │
│                                                                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐           │
│  │  Domain      │  │  DTOs        │  │  Streams     │           │
│  │  (Inmutable) │  │  (Records)   │  │  (Pure Fns)  │           │
│  └──────────────┘  └──────────────┘  └──────────────┘           │
│        │                  │                  │                   │
│  Money.add()         LoginResponse      parseAuthorities        │
│  (no modifica)       (sin setters)      (same in=same out)     │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📚 Conclusiones

Nova Commerce implementa correctamente los 4 principios porque:

1. **Abstracción + Encapsulamiento** → Hexagonal Architecture (Ports & Adapters)
2. **Herencia + Polimorfismo** → Strategy Pattern + Exception Hierarchy
3. **Inmutabilidad** → Money Value Objects + Record DTOs
4. **Funciones Puras + High-Order** → Stream API + Service Methods

Esta combinación proporciona:
- ✅ **Mantenibilidad:** Cambios localizados en adapters
- ✅ **Extensibilidad:** Nuevas estrategias sin modificar código
- ✅ **Testabilidad:** Funciones puras = tests predecibles
- ✅ **Seguridad:** Objetos inmutables = thread-safe
- ✅ **Escalabilidad:** Microservicios independientes

---

## 📖 Referencias Adicionales

**Patrones Implementados:**
- ✅ Hexagonal Architecture
- ✅ Clean Architecture
- ✅ Strategy Pattern (Discounts)
- ✅ Port & Adapter Pattern
- ✅ Repository Pattern
- ✅ Mapper Pattern (MapStruct)
- ✅ Factory Pattern (Money.of())

**Tecnologías:**
- Java 21 (Records, Pattern Matching)
- Spring Boot 3.4.3
- Spring Security 6.4.3
- Lombok (@Value, @Data)
- Stream API (Java Functional Programming)
- JJWT (JWT Tokens)
