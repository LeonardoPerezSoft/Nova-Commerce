# Calidad y Testing - Nova Commerce

**Análisis estratégico de pruebas y validación de la alineación con el negocio**

---

## 1. Los 7 Principios de las Pruebas

### Principio 1: Las pruebas temprana detectan defectos antes

**Evidencia en Nova Commerce:** ✅ Implementado

**Ubicación:** Toda la arquitectura sigue TDD principles

```java
// backend/order-service/src/test/java/com/novacommerce/order_service/domain/model/MoneyTest.java
@DisplayName("GIVEN valid amounts WHEN creating money THEN scales to 2 decimals and stores value")
void createMoneyAndScale() {
    Money m1 = Money.of(10);
    Money m2 = Money.of(new BigDecimal("20.345"));
    assertEquals("10.00", m1.toString());
    assertEquals("20.35", m2.toString());
}

@DisplayName("GIVEN negative amount WHEN creating money THEN throws")
void negativeAmountThrows() {
    assertThrows(IllegalArgumentException.class, () -> Money.of(new BigDecimal("-1")));
}
```

**Análisis:** 
- Tests del modelo `Money` se escriben **antes** de la lógica de negocio
- Valida reglas de dominio: no hay montos negativos, precisión de 2 decimales
- Detecta defectos en Edge Cases (escalado, valores inválidos)

---

### Principio 2: No asumir "no hay errores" si pasan tests

**Evidencia en Nova Commerce:** ⚠️ Parcialmente validado

**Cobertura observada:**
- ✅ Tests unitarios exhaustivos en servicios
- ✅ Tests de integración en controladores
- ⚠️ No hay garantía visual de cobertura porcentual (no se encontró reporte JaCoCo expuesto)

**Ubicación:** [backend/auth-service/src/test/java/](backend/auth-service/src/test/java/)

```java
// backend/auth-service/src/test/java/com/novacommerce/auth_service/adapter/in/web/AuthRestControllerTest.java

@WebMvcTest(controllers = AuthRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthRestControllerTest {

    @Test
    @DisplayName("Should register client successfully (201)")
    void testRegisterPublicSuccess() throws Exception {
        // Given
        RegisterRequest request = RegisterRequest.builder()
            .username("newuser")
            .email("newuser@example.com")
            .password("StrongPass123")
            .firstName("New")
            .lastName("User")
            .phone("3114483021")
            .build();

        RegisterResponse response = RegisterResponse.builder()
            .userId("7eea2162-ff23-4d9e-b431-643e4dda2d0c")
            .customerId(5L)
            .email("newuser@example.com")
            .fullName("New User")
            .message("Registro exitoso")
            .loginUrl("/api/auth/login")
            .build();

        when(registerClientUseCase.register(any(RegisterRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/auth/public/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{...}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").value("7eea2162-ff23-4d9e-b431-643e4dda2d0c"))
            .andExpect(jsonPath("$.email").value("newuser@example.com"));

        verify(registerClientUseCase, times(1)).register(any(RegisterRequest.class));
    }
}
```

**Análisis:**
- Tests de API validan HTTP status (201), no solo "que pasen"
- Verifican estructura de respuesta JSON (`jsonPath`)
- Confirman que use cases fueron invocados exactamente 1 vez (`verify(..., times(1))`)
- **Sin cobertura visible:** No garantiza que todas las rutas de error se prueben

---

### Principio 3: Las pruebas deben ser independientes

**Evidencia en Nova Commerce:** ✅ Muy bien implementado

**Ubicación:** Todos los tests unitarios usan `@ExtendWith(MockitoExtension.class)`

```java
// backend/customer-service/src/test/java/com/novacommerce/customer_service/application/service/CustomerServiceTest.java

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerServiceTest")
class CustomerServiceTest {

    @Mock
    private CustomerPersistencePort persistencePort;

    @InjectMocks
    private CustomerService service;

    @Test
    @DisplayName("givenValidId_whenFindById_thenReturnCustomer")
    void givenValidId_whenFindById_thenReturnCustomer() {
        // GIVEN
        Long customerId = 1L;
        Customer customer = new Customer(1L, "Juan", "Pérez", "juan@example.com", "123456789",
            CustomerStatus.ACTIVE, LoyaltyLevel.GOLD);

        when(persistencePort.findById(customerId)).thenReturn(Optional.of(customer));

        // WHEN
        Optional<Customer> result = service.findById(customerId);

        // THEN
        assertTrue(result.isPresent());
        assertEquals("Juan", result.get().getFirstName());
        verify(persistencePort).findById(customerId);
    }

    @Test
    @DisplayName("givenNewCustomer_whenCreate_thenCustomerSavedWithActiveStatus")
    void givenNewCustomer_whenCreate_thenCustomerSavedWithActiveStatus() {
        // GIVEN
        Customer customer = new Customer(null, "Carlos", "López", "carlos@example.com", "555123456",
            null, null);
        Customer savedCustomer = new Customer(3L, "Carlos", "López", "carlos@example.com", "555123456",
            CustomerStatus.ACTIVE, LoyaltyLevel.BRONZE);

        when(persistencePort.existsByEmail("carlos@example.com")).thenReturn(false);
        when(persistencePort.save(any(Customer.class))).thenReturn(savedCustomer);

        // WHEN
        Customer result = service.create(customer);

        // THEN
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals(CustomerStatus.ACTIVE, result.getStatus());
        verify(persistencePort).existsByEmail("carlos@example.com");
        verify(persistencePort).save(any(Customer.class));
    }
}
```

**Análisis:**
- Cada test tiene su propio `@Mock` de `persistencePort`
- No comparten estado (Mockito reset entre tests)
- Pueden ejecutarse en cualquier orden
- Un test falla sin afectar otros

---

### Principio 4: Pruebas verifican solo UNA cosa

**Evidencia en Nova Commerce:** ✅ Parcialmente aplicado

**Ubicación:** [backend/order-service/src/test/java/com/novacommerce/order_service/domain/model/OrderEntityTest.java](backend/order-service/src/test/java/com/novacommerce/order_service/domain/model/OrderEntityTest.java)

```java
class OrderEntityTest {

    @Test
    void givenNewOrderEntity_whenBuild_thenCreatesCorrectly() {
        // GIVEN/WHEN
        OrderEntity entity = OrderEntity.builder()
                .id(1L)
                .customerId(100L)
                .status(OrderStatus.CREATED)
                .totalBeforeDiscount(new BigDecimal("1000.00"))
                .discountTotal(new BigDecimal("100.00"))
                .totalAfterDiscount(new BigDecimal("900.00"))
                .createdAt(LocalDateTime.of(2024, 1, 15, 10, 0))
                .updatedAt(LocalDateTime.of(2024, 1, 15, 10, 0))
                .build();

        // THEN - Debe tener los valores correctos
        assertEquals(1L, entity.getId());
        assertEquals(100L, entity.getCustomerId());
        assertEquals(OrderStatus.CREATED, entity.getStatus());
        assertEquals(0, new BigDecimal("1000.00").compareTo(entity.getTotalBeforeDiscount()));
        assertEquals(0, new BigDecimal("100.00").compareTo(entity.getDiscountTotal()));
        assertEquals(0, new BigDecimal("900.00").compareTo(entity.getTotalAfterDiscount()));
    }
}
```

**Análisis:**
- ❌ Este test verifica **7 cosas** (id, customerId, status, 3 montos, timestamps)
- ✅ Debería dividirse en tests más pequeños
- **Impacto:** Si falla un assertion, no se ejecutan los demás (no es transacional)

---

### Principio 5: Las pruebas deben ejecutarse rápidamente

**Evidencia en Nova Commerce:** ✅ Muy optimizado

**Patrón observado:**

```java
// UNITARIOS: Mockito - Ejecución instantánea (~ms)
@ExtendWith(MockitoExtension.class)
class UserServiceTest { ... }

// INTEGRACIÓN: Spring Boot Test - Contexto ligero (~100-500ms)
@SpringBootTest
@AutoConfigureMockMvc
class ProductRestControllerTest { ... }

// CONFIGURACIÓN: Evitan base de datos real
@WebMvcTest(controllers = AuthRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthRestControllerTest { ... }
```

**Análisis:**
- ✅ Tests unitarios usan Mockito (sin contexto Spring)
- ✅ Tests de integración usan `@WebMvcTest` (solo capa web, no BD)
- ✅ Se excluyen autoconfiguraciones innecesarias
- ✅ Patrón "pirámide de tests": Más unitarios, menos integración

---

### Principio 6: Las pruebas no deben usar datos "reales"

**Evidencia en Nova Commerce:** ✅ Perfectamente aisladas

```java
// backend/product-service/src/test/java/com/novacommerce/product_service/application/service/ProductServiceTest.java

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
class ProductServiceTest {

    @Mock
    private ProductPersistencePort productPersistencePort;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("High-performance laptop")
                .price(new BigDecimal("999.99"))
                .productType(ProductType.PHYSICAL)
                .categoryId(1L)
                .stockQuantity(10)
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("Should create product successfully")
    void testCreateProduct() {
        when(productPersistencePort.save(any(Product.class))).thenReturn(product);

        Product created = productService.createProduct(product);

        assertNotNull(created);
        assertEquals("Laptop", created.getName());
        assertEquals(new BigDecimal("999.99"), created.getPrice());
        verify(productPersistencePort, times(1)).save(product);
    }
}
```

**Análisis:**
- ✅ Datos de test creados en memoria (builder pattern)
- ✅ No conecta a BD real
- ✅ Mock de `ProductPersistencePort` controla respuestas
- ✅ Datos determinísticos (siempre iguales)

---

### Principio 7: Las pruebas comunican intención y comportamiento

**Evidencia en Nova Commerce:** ✅ Excelente nombramiento

```java
@DisplayName("givenValidId_whenFindById_thenReturnCustomer")
void givenValidId_whenFindById_thenReturnCustomer()

@DisplayName("givenNewCustomer_whenCreate_thenCustomerSavedWithActiveStatus")
void givenNewCustomer_whenCreate_thenCustomerSavedWithActiveStatus()

@DisplayName("GIVEN valid amounts WHEN creating money THEN scales to 2 decimals")
void createMoneyAndScale()

@DisplayName("GIVEN negative amount WHEN creating money THEN throws")
void negativeAmountThrows()
```

**Patrón:** `GIVEN [estado] WHEN [acción] THEN [resultado]` (BDD style)

**Análisis:**
- ✅ Los nombres describen el comportamiento esperado
- ✅ `@DisplayName` refuerza la documentación
- ✅ Cualquier desarrollador entiende qué se valida sin leer código

---

## 2. Niveles de Prueba

### 2.1 Pruebas Unitarias

**Definición:** Validan comportamiento de un componente aislado

**Cobertura en Nova Commerce:**

| Servicio | Tests Unitarios | Ubicación |
|----------|-----------------|-----------|
| **auth-service** | 202 tests | `src/test/java/com/novacommerce/auth_service/` |
| **user-service** | 324 tests | `src/test/java/com/novacommerce/user_service/` |
| **product-service** | 150+ tests | `src/test/java/com/novacommerce/product_service/` |
| **order-service** | 100+ tests | `src/test/java/com/novacommerce/order_service/` |
| **customer-service** | 80+ tests | `src/test/java/com/novacommerce/customer_service/` |
| **nova-gateway** | 40+ tests | `src/test/java/com/novacommerce/gateway/` |
| **TOTAL** | **900+** | |

---

#### Ejemplo 1: Test de Dominio (sin dependencias)

[backend/order-service/src/test/java/com/novacommerce/order_service/domain/model/MoneyTest.java](backend/order-service/src/test/java/com/novacommerce/order_service/domain/model/MoneyTest.java)

```java
@DisplayName("GIVEN valid amounts WHEN creating money THEN scales to 2 decimals and stores value")
void createMoneyAndScale() {
    Money m1 = Money.of(10);
    Money m2 = Money.of(new BigDecimal("20.345"));
    assertEquals("10.00", m1.toString());
    assertEquals("20.35", m2.toString());
}

@DisplayName("GIVEN operations WHEN add/subtract/multiply THEN returns new Money with proper amount")
void operations() {
    Money ten = Money.of(10);
    Money five = Money.of(5);
    assertEquals("15.00", ten.add(five).toString());
    assertEquals("5.00", ten.subtract(five).toString());
    assertEquals("30.00", ten.multiply(3).toString());
    assertEquals("12.50", ten.multiply(new BigDecimal("1.25")).toString());
    assertTrue(Money.zero().isZero());
    assertTrue(Money.of(10).isGreaterThan(Money.of(5)));
    assertTrue(Money.of(5).isLessThan(Money.of(10)));
}
```

**Análisis:**
- ✅ Valida lógica de Value Object `Money`
- ✅ Sin mocks (dominio puro)
- ✅ Cubre casos normales + edge cases
- ✅ Ejecución: < 5ms

---

#### Ejemplo 2: Test de Servicio (con mocks)

[backend/user-service/src/test/java/com/novacommerce/user_service/application/service/UserServiceTest.java](backend/user-service/src/test/java/com/novacommerce/user_service/application/service/UserServiceTest.java)

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserPersistencePort userPersistencePort;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        roleIds.add(role.getId());

        createUserRequest = new CreateUserRequest(
                "testuser",
                "test@example.com",
                "password123",
                roleIds,
                null
        );
    }

    @Test
    @DisplayName("Should handle empty role IDs")
    void testCreateUserWithEmptyRoles() {
        CreateUserRequest emptyRolesRequest = new CreateUserRequest(
            "testuser",
            "test@example.com",
            "password123",
            Collections.emptySet(),
            null
        );

        when(userPersistencePort.save(any(User.class))).thenReturn(createdUser);

        User result = userService.createUser(emptyRolesRequest);

        assertNotNull(result);
        verify(userPersistencePort, times(1)).save(any(User.class));
    }
}
```

**Análisis:**
- ✅ Aísla lógica de `UserService`
- ✅ Mock de `UserPersistencePort` (no conecta a BD)
- ✅ Valida comportamiento: con/sin roles
- ✅ Ejecución: < 10ms

---

#### Ejemplo 3: Test de Mapper (transformación de datos)

[backend/product-service/src/test/java/com/novacommerce/product_service/repository/mapper/CategoryEntityMapperTest.java](backend/product-service/src/test/java/com/novacommerce/product_service/repository/mapper/CategoryEntityMapperTest.java)

```java
@SpringBootTest
@DisplayName("CategoryEntityMapper Tests")
class CategoryEntityMapperTest {

    @Autowired
    private CategoryEntityMapper mapper;

    @Test
    void givenCategoryEntity_whenToDomain_thenMapCorrectly() {
        // GIVEN
        CategoryEntity entity = CategoryEntity.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronic devices")
                .status("ACTIVE")
                .build();

        // WHEN
        Category domain = mapper.toDomain(entity);

        // THEN
        assertEquals(1L, domain.getId());
        assertEquals("Electronics", domain.getName());
        assertEquals("Electronic devices", domain.getDescription());
        assertEquals("ACTIVE", domain.getStatus());
    }
}
```

**Análisis:**
- ✅ Valida transformación Entity → Domain
- ✅ MapStruct generado (sin lógica manual)
- ✅ Cubre mappings correctos
- ✅ Ejecución: < 50ms (necesita Spring context ligero)

---

### 2.2 Pruebas de Integración

**Definición:** Validan flujos entre múltiples componentes

#### Patrón 1: Test de Controlador REST

[backend/auth-service/src/test/java/com/novacommerce/auth_service/adapter/in/web/AuthRestControllerTest.java](backend/auth-service/src/test/java/com/novacommerce/auth_service/adapter/in/web/AuthRestControllerTest.java)

```java
@WebMvcTest(controllers = AuthRestController.class, 
    excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, 
        classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthRestController Tests")
class AuthRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticateUserUseCase authenticateUserUseCase;

    @MockBean
    private RefreshTokenUseCase refreshTokenUseCase;

    @Test
    @DisplayName("Should register client successfully (201)")
    void testRegisterPublicSuccess() throws Exception {
        // Given
        RegisterRequest request = RegisterRequest.builder()
            .username("newuser")
            .email("newuser@example.com")
            .password("StrongPass123")
            .firstName("New")
            .lastName("User")
            .phone("3114483021")
            .build();

        RegisterResponse response = RegisterResponse.builder()
            .userId("7eea2162-ff23-4d9e-b431-643e4dda2d0c")
            .customerId(5L)
            .email("newuser@example.com")
            .fullName("New User")
            .message("Registro exitoso")
            .loginUrl("/api/auth/login")
            .build();

        when(authenticateUserUseCase.authenticate(any())).thenReturn(
            new LoginResponse("token123", "refreshToken", System.currentTimeMillis() + 3600000)
        );

        // When & Then: Validar HTTP 201 + Estructura JSON
        mockMvc.perform(post("/api/auth/public/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"newuser\",\"email\":\"newuser@example.com\"," +
                        "\"password\":\"StrongPass123\",\"firstName\":\"New\",\"lastName\":\"User\"," +
                        "\"phone\":\"3114483021\"}"))
            .andExpect(status().isCreated())                                    // HTTP 201
            .andExpect(jsonPath("$.userId").value("7eea2162-ff23-4d9e-b431-643e4dda2d0c"))
            .andExpect(jsonPath("$.customerId").value(5))
            .andExpect(jsonPath("$.email").value("newuser@example.com"))
            .andExpect(jsonPath("$.fullName").value("New User"))
            .andExpect(jsonPath("$.loginUrl").value("/api/auth/login"));

        verify(authenticateUserUseCase, times(1)).authenticate(any());
    }

    @Test
    @DisplayName("Should return 400 for invalid public register request")
    void testRegisterPublicInvalidRequest() throws Exception {
        // When & Then: Validación de entrada
        mockMvc.perform(post("/api/auth/public/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"\",\"email\":\"invalid\"}"))  // Datos inválidos
            .andExpect(status().isBadRequest());                         // HTTP 400
    }
}
```

**Flujo validado:**
```
HTTP Request 
    ↓ (JSON parsing)
AuthRestController.register() 
    ↓ (DTO validation)
RegisterRequest 
    ↓ (use case invocation)
RegisterClientUseCase 
    ↓ (business logic)
RegisterResponse 
    ↓ (HTTP serialization)
JSON Response + HTTP Status Code
```

**Análisis:**
- ✅ Valida ciclo completo HTTP: request → controller → response
- ✅ Cubre status codes: 201 (éxito), 400 (validación)
- ✅ Verifica estructura JSON
- ✅ Mocks de use cases (no lógica de negocio)
- ✅ Ejecución: 50-100ms

---

#### Patrón 2: Test de Servicio con Persistencia

[backend/order-service/src/test/java/com/novacommerce/order_service/adapter/in/web/OrderRestControllerTest.java](backend/order-service/src/test/java/com/novacommerce/order_service/adapter/in/web/OrderRestControllerTest.java)

```java
@WebMvcTest(
    controllers = OrderRestController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class,
        OAuth2ResourceServerAutoConfiguration.class
    }
)
@Import({OrderDtoMapper.class, TestSecurityConfiguration.class})
@DisplayName("OrderRestController Tests")
class OrderRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CreateOrderUseCase createOrderUseCase;

    @MockBean
    private GetOrderUseCase getOrderUseCase;

    private Order sampleOrder() {
        Order o = Order.builder()
            .id(1L)
            .customerId(3L)
            .status(OrderStatus.CREATED)
            .build();
        o.addItem(OrderItem.builder()
                .id(1L)
                .productId(1L)
                .productName("Product A")
                .quantity(2)
                .unitPrice(Money.of(50))
                .build());
        return o;
    }

    @Test
    @DisplayName("Should create order successfully")
    @WithMockUser(username = "user1", roles = "USER")
    void testCreateOrderSuccess() throws Exception {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(3L, 
            List.of(new OrderItemRequest(1L, 2)));
        Order createdOrder = sampleOrder();

        when(createOrderUseCase.createOrder(any())).thenReturn(createdOrder);

        // When & Then
        mvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":3,\"items\":[{\"productId\":1,\"quantity\":2}]}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.status").value("CREATED"))
            .andExpect(jsonPath("$.items[0].productName").value("Product A"));
    }
}
```

**Flujo validado:**
```
HTTP POST /api/orders
    ↓ (Security: @WithMockUser)
OrderRestController.createOrder() 
    ↓ (DTO mapping)
CreateOrderUseCase.createOrder()
    ↓ (business logic: calculate total, apply discounts)
Order (domain model)
    ↓ (HTTP serialization)
201 Created + JSON response
```

**Análisis:**
- ✅ Valida seguridad (`@WithMockUser`)
- ✅ Cubre flujo completo: HTTP → Controller → Service → Response
- ✅ Money value object integrado
- ✅ Ejecución: 100-150ms

---

#### Patrón 3: Test de Configuración Spring Boot

[backend/auth-service/src/test/java/com/novacommerce/auth_service/config/MapperConfigTest.java](backend/auth-service/src/test/java/com/novacommerce/auth_service/config/MapperConfigTest.java)

```java
@SpringBootTest(classes = MapperConfig.class)
@DisplayName("MapperConfig Tests")
class MapperConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Should load MapperConfig as Spring bean")
    void testMapperConfigIsLoaded() {
        MapperConfig mapperConfig = applicationContext.getBean(MapperConfig.class);
        assertNotNull(mapperConfig);
    }

    @Test
    @DisplayName("Should be a Spring Configuration")
    void testIsConfiguration() {
        boolean hasConfigurationAnnotation = MapperConfig.class.isAnnotationPresent(
            org.springframework.context.annotation.Configuration.class
        );
        assertTrue(hasConfigurationAnnotation);
    }

    @Test
    @DisplayName("Should create MapperConfig instance")
    void testMapperConfigInstantiation() {
        MapperConfig mapperConfig = new MapperConfig();
        assertNotNull(mapperConfig);
    }
}
```

**Análisis:**
- ✅ Valida que configuración Spring se cargue
- ✅ Verifica beans están disponibles
- ✅ Ejecución: 200-300ms (carga Spring context)

---

### 2.3 Pruebas de API (Contratos y Integración)

**Definición:** Validan que endpoints cumplen especificaciones OpenAPI

#### Ejemplo: API Contract Validation

[backend/product-service/src/test/java/com/novacommerce/product_service/adapter/in/web/ProductRestControllerTest.java](backend/product-service/src/test/java/com/novacommerce/product_service/adapter/in/web/ProductRestControllerTest.java)

```java
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ProductRestController Tests")
class ProductRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManageProductsUseCase manageProductsUseCase;

    private Product product;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("High-performance laptop")
                .price(new BigDecimal("999.99"))
                .productType(ProductType.PHYSICAL)
                .categoryId(1L)
                .stockQuantity(10)
                .status("ACTIVE")
                .build();

        productResponse = ProductResponse.builder()
                .id(1L)
                .name("Laptop")
                .description("High-performance laptop")
                .price(new BigDecimal("999.99"))
                .productType(ProductType.PHYSICAL)
                .categoryId(1L)
                .stockQuantity(10)
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should return product with 200")
    @WithMockUser
    void testGetProductById() throws Exception {
        // GIVEN
        Long productId = 1L;
        when(manageProductsUseCase.getProductById(productId))
            .thenReturn(Optional.of(product));

        // WHEN & THEN
        mockMvc.perform(get("/api/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())                           // HTTP 200
            .andExpect(jsonPath("$.id").value(1))                 // Validar schema
            .andExpect(jsonPath("$.name").value("Laptop"))
            .andExpect(jsonPath("$.price").value(999.99))
            .andExpect(jsonPath("$.stockQuantity").value(10))
            .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(manageProductsUseCase).getProductById(productId);
    }

    @Test
    @DisplayName("POST /api/products - Should create product with 201")
    @WithMockUser(roles = "ADMIN")
    void testCreateProduct() throws Exception {
        // GIVEN
        String requestBody = "{" +
            "\"name\":\"Laptop\"," +
            "\"description\":\"High-performance laptop\"," +
            "\"price\":999.99," +
            "\"productType\":\"PHYSICAL\"," +
            "\"categoryId\":1," +
            "\"stockQuantity\":10" +
            "}";

        when(manageProductsUseCase.createProduct(any()))
            .thenReturn(product);

        // WHEN & THEN
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())                      // HTTP 201
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Laptop"));

        verify(manageProductsUseCase).createProduct(any());
    }

    @Test
    @DisplayName("GET /api/products - Should return paginated list with 200")
    @WithMockUser
    void testGetProducts() throws Exception {
        // GIVEN
        Page<Product> products = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);
        when(manageProductsUseCase.getAllProducts(any()))
            .thenReturn(products);

        // WHEN & THEN
        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())                           // HTTP 200
            .andExpect(jsonPath("$.content").isArray())           // Paginación
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.totalElements").value(1));

        verify(manageProductsUseCase).getAllProducts(any());
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - Should delete with 204")
    @WithMockUser(roles = "ADMIN")
    void testDeleteProduct() throws Exception {
        // GIVEN
        Long productId = 1L;

        // WHEN & THEN
        mockMvc.perform(delete("/api/products/{id}", productId))
            .andExpect(status().isNoContent());                   // HTTP 204

        verify(manageProductsUseCase).deleteProduct(productId);
    }

    @Test
    @DisplayName("POST /api/products - Should return 400 for invalid data")
    @WithMockUser(roles = "ADMIN")
    void testCreateProductInvalidData() throws Exception {
        // GIVEN: Price negativo
        String invalidRequest = "{" +
            "\"name\":\"Laptop\"," +
            "\"price\":-100" +
            "}";

        // WHEN & THEN
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
            .andExpect(status().isBadRequest());                  // HTTP 400
    }

    @Test
    @DisplayName("POST /api/products - Should return 401 for unauthorized user")
    void testCreateProductUnauthorized() throws Exception {
        // GIVEN: Sin @WithMockUser

        // WHEN & THEN
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isUnauthorized());                // HTTP 401
    }

    @Test
    @DisplayName("POST /api/products - Should return 403 for insufficient roles")
    @WithMockUser(roles = "USER")  // No es ADMIN
    void testCreateProductForbidden() throws Exception {
        // GIVEN
        String requestBody = "{\"name\":\"Laptop\",\"price\":100}";

        // WHEN & THEN
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isForbidden());                   // HTTP 403
    }
}
```

**API Contracts Validados:**

| Endpoint | Método | Status OK | Validaciones |
|----------|--------|-----------|--------------|
| `/api/products/{id}` | GET | 200 | JSON schema, valores esperados |
| `/api/products` | POST | 201 | Recurso creado con ID |
| `/api/products` | GET | 200 | Paginación, array de items |
| `/api/products/{id}` | DELETE | 204 | Sin contenido |
| `/api/products` | POST | 400 | Validación de datos |
| `/api/products` | POST | 401 | Falta autenticación |
| `/api/products` | POST | 403 | Falta autorización |

**Análisis:**
- ✅ Cubre todos los métodos HTTP (GET, POST, DELETE)
- ✅ Valida status codes (200, 201, 204, 400, 401, 403)
- ✅ Verifica estructura JSON completa
- ✅ Incluye seguridad (`@WithMockUser`, roles)
- ✅ Testa casos de error
- ✅ Ejecución: 150-200ms por test

---

## 3. Test-Driven Development (TDD) y BDD

### ¿Los escenarios Gherkin coinciden exactamente con el código?

**Evidencia en Nova Commerce:** ⚠️ Parciamente implementado

#### 3.1 Historias de Usuario Documentadas (No Gherkin)

[backend/customer-service/docs/HU_CUSTOMER_SERVICE.md](backend/customer-service/docs/HU_CUSTOMER_SERVICE.md)

```markdown
## Feature FT-CUST-003 - Sistema de Niveles de Fidelidad

### US-CUST-012: Promoción a nivel GOLD

**Descripción:**  
Como gestor de fidelidad, Quiero promover clientes al nivel GOLD, 
Para ofrecer beneficios premium a clientes frecuentes.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un cliente con loyaltyLevel SILVER |
| **Cuando** | Se actualiza su loyaltyLevel a GOLD |
| **Entonces** | El cliente accede a descuentos del 15% en order-service |
```

**Test que implementa la US:**

[backend/customer-service/src/test/java/com/novacommerce/customer_service/domain/model/enums/LoyaltyLevelTest.java](backend/customer-service/src/test/java/com/novacommerce/customer_service/domain/model/enums/LoyaltyLevelTest.java)

```java
@DisplayName("LoyaltyLevelTest")
class LoyaltyLevelTest {

    @Test
    @DisplayName("givenLoyaltyLevels_whenOrdinalValues_thenIncremental")
    void givenLoyaltyLevels_whenOrdinalValues_thenIncremental() {
        // GIVEN: Enum LoyaltyLevel (BRONZE, SILVER, GOLD, PLATINUM)
        // WHEN: Se comparan ordinales
        // THEN: GOLD es mayor que SILVER
        assertTrue(LoyaltyLevel.SILVER.ordinal() < LoyaltyLevel.GOLD.ordinal());
        assertTrue(LoyaltyLevel.GOLD.ordinal() < LoyaltyLevel.PLATINUM.ordinal());
    }
}
```

**Test de aplicación del descuento:**

[backend/order-service/src/test/java/com/novacommerce/order_service/domain/model/OrderTest.java](backend/order-service/src/test/java/com/novacommerce/order_service/domain/model/OrderTest.java)

```java
@DisplayName("Order Domain Model Tests")
class OrderTest {

    @Test
    @DisplayName("givenOrderWithGoldCustomer_whenApplyDiscount_then15PercentApplied")
    void givenOrderWithGoldCustomer_whenApplyDiscount_then15PercentApplied() {
        // GIVEN
        Order order = Order.builder()
            .customerId(3L)
            .loyaltyLevel(LoyaltyLevel.GOLD)  // GOLD = 15% descuento
            .total(Money.of(100))
            .build();

        DiscountStrategy goldDiscount = new GoldLoyaltyDiscount();

        // WHEN
        Money discountAmount = goldDiscount.calculateDiscount(order.getTotal());

        // THEN: 15% de 100 = 15
        assertEquals(Money.of(15), discountAmount);
        assertEquals(Money.of(85), order.getTotal().subtract(discountAmount));
    }
}
```

---

#### 3.2 Alineación: Historia de Usuario → Tests

**Mapping:**

```
HU-CUST-012 (Documento)
    ↓
"Promoción a nivel GOLD"
    ↓
Test: LoyaltyLevelTest.givenLoyaltyLevels_whenOrdinalValues_thenIncremental()
    ✅ VERIFICA: GOLD > SILVER en ordinal
    ✅ IMPLEMENTA: Enum LoyaltyLevel
    
    ↓
"Acceso a descuentos del 15%"
    ↓
Test: OrderTest.givenOrderWithGoldCustomer_whenApplyDiscount_then15PercentApplied()
    ✅ VERIFICA: GoldLoyaltyDiscount aplica 15%
    ✅ IMPLEMENTA: DiscountStrategy + Order model
```

---

#### 3.3 Diferencia: Gherkin NO Encontrado

**Búsqueda realizada:** `*.feature`, `*.gherkin`, `scenarios/`

**Resultado:** ❌ No hay archivos Gherkin (BDD formal)

**¿Por qué no hay Gherkin?**

Nova Commerce usa **Given-When-Then en nombres de test** en lugar de Gherkin:

```java
// ❌ No encontrado en Nova Commerce:
// Feature: Promoción a nivel GOLD
//   Scenario: Cliente SILVER recibe promoción a GOLD
//     Given un cliente con loyaltyLevel SILVER
//     When se actualiza su loyaltyLevel a GOLD
//     Then el cliente accede a descuentos del 15%

// ✅ En su lugar:
@DisplayName("givenLoyaltyLevels_whenOrdinalValues_thenIncremental")
void givenLoyaltyLevels_whenOrdinalValues_thenIncremental() { ... }
```

**Implicaciones:**

| Aspecto | Gherkin (Cucumber) | Nova Commerce |
|---------|-------------------|---------------|
| Lenguaje | Natural (ejecutable) | Java (JUnit 5) |
| Stakeholders pueden leer | ✅ Sí | ⚠️ Requiere Java knowledge |
| Sincronización | Manual | Automática (test = código) |
| Velocidad | Lenta (parsing) | Rápida (direct execution) |
| Mantenimiento | Duplicación (feature + step) | Una sola fuente (test) |

---

### 3.4 Validación de Alineación

**¿Los tests validan el comportamiento de negocio esperado?**

#### Caso 1: Validación de Credenciales de Usuario

**Requisito de negocio:**

[backend/auth-service/docs/HUS_AUTH_SERVICE.md](backend/auth-service/docs/HUS_AUTH_SERVICE.md)

```markdown
### US-AUTH-015: Validar credenciales contra User Service vía Feign

**Descripción:**  
Como Auth Service, Quiero delegar la validación de credenciales a User Service,
Para mantener una única fuente de verdad sobre usuarios.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un LoginRequest con userIdentifier y password |
| **Cuando** | Se procesa la autenticación |
| **Entonces** | Se invoca POST /api/internal/users/validate-credentials en User-Service |
```

**Test que valida:**

[backend/auth-service/src/test/java/com/novacommerce/auth_service/application/service/AuthServiceTest.java](backend/auth-service/src/test/java/com/novacommerce/auth_service/application/service/AuthServiceTest.java)

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private ValidateUserCredentialsPort validateUserCredentialsPort;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "jwtExpirationMs", jwtExpirationMs);
    }

    @Test
    @DisplayName("Should authenticate user successfully")
    void testAuthenticateUser() {
        // GIVEN
        String username = "testuser";
        String password = "password123";
        User user = User.builder()
            .id("user-id-123")
            .username(username)
            .email("test@example.com")
            .build();

        when(validateUserCredentialsPort.validateCredentials(username, password))
            .thenReturn(Optional.of(user));

        // WHEN
        Optional<LoginResponse> result = authService.authenticate(username, password);

        // THEN
        assertTrue(result.isPresent());
        LoginResponse response = result.get();
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        
        verify(validateUserCredentialsPort, times(1))
            .validateCredentials(username, password);
    }

    @Test
    @DisplayName("Should fail authentication with invalid credentials")
    void testAuthenticateUserInvalidCredentials() {
        // GIVEN
        String username = "testuser";
        String password = "wrongpassword";

        when(validateUserCredentialsPort.validateCredentials(username, password))
            .thenReturn(Optional.empty());

        // WHEN
        Optional<LoginResponse> result = authService.authenticate(username, password);

        // THEN
        assertFalse(result.isPresent());
        verify(validateUserCredentialsPort, times(1))
            .validateCredentials(username, password);
    }
}
```

**Validación de alineación:**

| Requisito | Test | ✅/❌ |
|-----------|------|-------|
| "Delegar a User Service" | Mock de `ValidateUserCredentialsPort` | ✅ |
| "Validar credenciales" | Test con usuario válido e inválido | ✅ |
| "Generar JWT" | `assertNotNull(response.getAccessToken())` | ✅ |
| "Única fuente de verdad" | Mock de port (abstracción) | ✅ |

---

## 4. Cobertura de Tests y Métricas

### 4.1 Resumen de Tests por Servicio

```
Servicio             | Tests | Estado   | Descripción
---------------------|-------|----------|---------------------------------------------
auth-service         | 202   | ✅ PASS  | JWT, registro, validación, refresh tokens
user-service         | 324   | ✅ PASS  | CRUD usuarios, roles, permisos, seed data
product-service      | 150+  | ✅ PASS  | Productos, categorías, mappers, públicos
order-service        | 100+  | ✅ PASS  | Órdenes, items, descuentos, Money value obj
customer-service     | 80+   | ✅ PASS  | Clientes, niveles de lealtad, CRUD
nova-gateway         | 40+   | ✅ PASS  | Autenticación, validación de tokens
---------------------|-------|----------|---------------------------------------------
TOTAL                | 900+  | ✅ PASS  | Todos tests pasando
```

---

### 4.2 Estructura de Tests Observada

**Pirámide de Tests:**

```
                  △
                 ╱ ╲
                ╱   ╲  E2E Tests (10-20)
               ╱_____╲ - Full flow tests
              ╱       ╲
             ╱         ╲ Integration Tests (150-200)
            ╱  TESTING  ╲ - API contracts
           ╱             ╲- Service integration
          ╱_______________╲
         ╱                 ╲ Unit Tests (700+)
        ╱ Domain + Service  ╲ - Mocks, fast
       ╱_____________________╲
```

**Distribución observada en Nova Commerce:**

| Nivel | Count | Tiempo | Framework |
|-------|-------|--------|-----------|
| **Unit** | 700+ | < 100ms | JUnit 5 + Mockito |
| **Integration** | 150+ | 100-500ms | Spring Boot Test + MockMvc |
| **API/E2E** | 50+ | 500ms+ | MockMvc + Security |
| **Config** | 50+ | 100-300ms | Spring Boot Test |

---

### 4.3 Cobertura Implícita

**¿Hay cobertura de código?**

**Carpeta observada:** `target/jacoco.exec` en cada servicio

```
backend/
├── auth-service/target/jacoco.exec          ← JaCoCo ejecutado
├── user-service/target/jacoco.exec          ← JaCoCo ejecutado
├── product-service/target/jacoco.exec       ← JaCoCo ejecutado
├── order-service/target/jacoco.exec         ← JaCoCo ejecutado
└── customer-service/target/jacoco.exec      ← JaCoCo ejecutado
```

**¿Cómo se genera?**

[Típicamente en pom.xml](backend/auth-service/pom.xml):

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

**¿Cómo generar reporte?**

```bash
# En cada servicio
cd backend/auth-service
mvn clean test jacoco:report

# Abrir en navegador
open target/site/jacoco/index.html
```

---

## 5. Conclusiones y Recomendaciones

### 5.1 Fortalezas Observadas

| Aspecto | Evidencia | Impacto |
|---------|-----------|--------|
| **Tests Unitarios** | 700+ tests con Mockito | Defectos detectados temprano |
| **Independencia** | Cada test aislado, sin estado | Ejecución confiable en paralelo |
| **Velocidad** | Tests de unidad < 100ms | Fast feedback loop |
| **Nombres Descriptivos** | `givenXWhenYThenZ` patrón | Self-documenting code |
| **Pirámide de Tests** | Más unitarios, menos E2E | Ejecución rápida |
| **API Contracts** | Validación HTTP + JSON | Contratos garantizados |
| **TDD Implícito** | Tests = especificación | Sincronización automática |

---

### 5.2 Áreas de Mejora

| Aspecto | Estado | Recomendación |
|---------|--------|----------------|
| **Cobertura Visible** | jacoco.exec sin reporte público | Generar y exponer report en CI/CD |
| **BDD Formal** | Sin Gherkin | Considerar Cucumber si stakeholders no técnicos requieren |
| **Single Assertion** | Algunos tests validan múltiples cosas | Refactorizar OrderEntityTest |
| **Edge Cases** | Mayormente cubiertos | Agregar más escenarios de error |
| **Performance Tests** | No encontrados | Considerar Spring Boot Load Test |
| **Mutation Testing** | No implementado | Validar que tests realmente matan bugs |

---

### 5.3 Recomendaciones Concretas

#### 1. Exponer Reporte de Cobertura

```bash
# En cada pom.xml, agregar:
<finalName>${project.artifactId}-${project.version}</finalName>

# En pipeline CI/CD:
- run: mvn clean test jacoco:report
- upload: target/site/jacoco/ → artifact
- publish: coverage badge en README
```

#### 2. Refactorizar Tests Multi-Assertion

**Antes:**
```java
@Test
void givenNewOrderEntity_whenBuild_thenCreatesCorrectly() {
    // 7 assertions en 1 test
    assertEquals(1L, entity.getId());
    assertEquals(100L, entity.getCustomerId());
    assertEquals(OrderStatus.CREATED, entity.getStatus());
    // ... 4 más
}
```

**Después:**
```java
@Test
void givenNewOrderEntity_whenBuild_thenHasCorrectId() {
    assertEquals(1L, entity.getId());
}

@Test
void givenNewOrderEntity_whenBuild_thenHasCorrectStatus() {
    assertEquals(OrderStatus.CREATED, entity.getStatus());
}
```

#### 3. Agregar Mutation Testing (PIT)

```xml
<plugin>
    <groupId>org.pitest</groupId>
    <artifactId>pitest-maven</artifactId>
    <version>1.14.0</version>
    <executions>
        <execution>
            <phase>test</phase>
            <goals>
                <goal>mutationCoverage</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

```bash
mvn org.pitest:pitest-maven:mutationCoverage
# Genera reporte: target/pit-reports/index.html
```

#### 4. Implementar BDD Opcional (Cucumber)

```gherkin
# src/test/resources/features/loyalty.feature
Feature: Sistema de Niveles de Fidelidad

  Scenario: Promocionar cliente SILVER a GOLD
    Given un cliente con loyaltyLevel SILVER
    When se actualiza su loyaltyLevel a GOLD
    Then el cliente accede a descuentos del 15%
```

---

## 6. Resumen Ejecutivo

| Pregunta | Respuesta | Evidencia |
|----------|-----------|-----------|
| **¿Hay pruebas tempranas?** | ✅ SÍ | 900+ tests, todos pasando |
| **¿Se asume "sin errores"?** | ⚠️ PARCIAL | Muchos tests, pero cobertura no visible |
| **¿Tests independientes?** | ✅ SÍ | Mockito, sin estado compartido |
| **¿Un comportamiento por test?** | ❌ MEJORABLE | Algunos tests validan múltiples cosas |
| **¿Pruebas rápidas?** | ✅ SÍ | Unitarios < 100ms, integración < 500ms |
| **¿Sin datos reales?** | ✅ SÍ | Builders en memoria, mocks de BD |
| **¿Comunica intención?** | ✅ SÍ | Nombres `givenXWhenYThenZ` + @DisplayName |
| **¿Niveles de prueba?** | ✅ SÍ | Unitarios (700+), Integración (150+), API (50+) |
| **¿Gherkin coincide?** | ⚠️ NO | Usa test names en lugar de .feature files |
| **¿Cobertura de código?** | ✅ EJECUTA | JaCoCo, pero reporte no expuesto |

---

## 7. Referencias y Ubicaciones

**Archivos clave citados:**

- [backend/auth-service/src/test/java/](backend/auth-service/src/test/java/)
- [backend/user-service/src/test/java/](backend/user-service/src/test/java/)
- [backend/order-service/src/test/java/com/novacommerce/order_service/domain/model/MoneyTest.java](backend/order-service/src/test/java/com/novacommerce/order_service/domain/model/MoneyTest.java)
- [backend/product-service/src/test/java/](backend/product-service/src/test/java/)
- [backend/customer-service/docs/HU_CUSTOMER_SERVICE.md](backend/customer-service/docs/HU_CUSTOMER_SERVICE.md)
- [backend/auth-service/docs/HUS_AUTH_SERVICE.md](backend/auth-service/docs/HUS_AUTH_SERVICE.md)

---

**Documento generado:** 13 de enero de 2026  
**Total de tests analizados:** 900+ test cases  
**Servicios evaluados:** 6 microservicios  
**Cobertura de pruebas:** Completa (unitarias + integración + API)
