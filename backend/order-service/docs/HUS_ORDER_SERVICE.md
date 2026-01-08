
# Historias de Usuario - Order Service (NovaCommerce)

## 📋 Épica Global
**EP-GLOBAL-001** - Plataforma de E-commerce Modular y Escalable

## 📦 Épica Order Service
**EP-ORD-001** - Gestión de Órdenes de Compra

---

## Feature FT-ORD-001 - Creación y Validación de Órdenes

### US-ORD-001: Crear orden válida con cliente activo

**Descripción:**  
Como sistema de ventas, Quiero procesar la creación de órdenes para clientes activos con stock, Para registrar transacciones comerciales válidas.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un cliente con estatus ACTIVE y productos disponibles en stock |
| **Cuando** | Se invoca POST /api/orders con items válidos |
| **Entonces** | La orden se crea y se guarda correctamente, retornando HTTP 200 con los datos de la orden |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-ORD-001 - Creación y Validación de Órdenes
- **Épica:** EP-ORD-001 - Gestión de Órdenes
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

---

### US-ORD-002: Rechazar orden con cliente inactivo

**Descripción:**  
Como sistema de ventas, Quiero validar que el cliente esté activo, Para evitar transacciones con cuentas inhabilitadas.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un cliente con estatus INACTIVE |
| **Cuando** | Se intenta crear una orden |
| **Entonces** | Se rechaza con excepción CUSTOMER_INACTIVE y no se persiste la orden |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-ORD-001
- **Épica:** EP-ORD-001
- **Dependencias:** US-ORD-001
- **Versión/Release:** 1.0

---

### US-ORD-003: Rechazar orden con cliente bloqueado

**Descripción:**  
Como sistema de ventas, Quiero validar el estado de bloqueo del cliente, Para prevenir fraudes o riesgos operativos.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un cliente con estatus BLOCKED |
| **Cuando** | Se intenta crear una orden |
| **Entonces** | Se rechaza con excepción CUSTOMER_BLOCKED y no se persiste la orden |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-ORD-001
- **Épica:** EP-ORD-001
- **Dependencias:** US-ORD-001
- **Versión/Release:** 1.0

---

### US-ORD-004: Rechazar orden con cliente inexistente

**Descripción:**  
Como sistema de ventas, Quiero validar la existencia del cliente, Para asegurar la integridad de los datos de la orden.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un customerId que no existe en el sistema |
| **Cuando** | Se intenta crear una orden |
| **Entonces** | Se rechaza con excepción CUSTOMER_NOT_FOUND y no se persiste la orden |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-ORD-001
- **Épica:** EP-ORD-001
- **Dependencias:** US-ORD-001
- **Versión/Release:** 1.0

---

### US-ORD-005: Rechazar orden por stock insuficiente

**Descripción:**  
Como sistema de inventario, Quiero validar la disponibilidad física de los productos, Para no comprometer ventas sin existencias.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un producto con stock insuficiente |
| **Cuando** | Se intenta crear una orden con cantidad mayor al stock |
| **Entonces** | Se rechaza con excepción INSUFFICIENT_STOCK y no se persiste la orden |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-ORD-001
- **Épica:** EP-ORD-001
- **Dependencias:** US-ORD-001
- **Versión/Release:** 1.0

---

### US-ORD-006: Rechazar orden con producto inválido

**Descripción:**  
Como sistema de ventas, Quiero verificar que el producto solicitado exista, Para evitar errores en la facturación y despacho.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un productId que no existe en el sistema |
| **Cuando** | Se intenta crear una orden |
| **Entonces** | Se rechaza con excepción PRODUCT_NOT_FOUND y no se persiste la orden |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-ORD-001
- **Épica:** EP-ORD-001
- **Dependencias:** US-ORD-001
- **Versión/Release:** 1.0

---

### US-ORD-007: Calcular totales correctamente

**Descripción:**  
Como sistema contable, Quiero calcular los montos brutos y netos de la orden, Para garantizar que el cobro al cliente sea exacto.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Items con precios unitarios y cantidades |
| **Cuando** | Se crea una orden |
| **Entonces** | Calcula totalBeforeDiscount = Σ(unitPrice × quantity) y totalAfterDiscount es correcto aplicando reglas base |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-ORD-001
- **Épica:** EP-ORD-001
- **Dependencias:** US-ORD-001
- **Versión/Release:** 1.0

---

## Feature FT-ORD-002 - Sistema de Descuentos Multicapa

### US-ORD-008: Aplicar descuento por fidelidad (GOLD)

**Descripción:**  
Como sistema de beneficios, Quiero aplicar un descuento especial a clientes GOLD, Para incentivar la lealtad de los mejores compradores.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un cliente con loyaltyLevel = GOLD |
| **Cuando** | Se crea una orden |
| **Entonces** | Se aplica descuento del 15% al total y el discountTotal refleja el monto descontado |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-ORD-002 - Sistema de Descuentos Multicapa
- **Épica:** EP-ORD-002 - Reglas de Negocio y Precios
- **Dependencias:** US-ORD-001, US-ORD-007
- **Versión/Release:** 1.1

---

### US-ORD-009: Aplicar descuento por tipo de producto

**Descripción:**  
Como sistema de promociones, Quiero aplicar descuentos según la categoría del producto, Para impulsar las ventas en departamentos específicos.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Items con categorías ELECTRONICS o CLOTHING |
| **Cuando** | Se crea una orden |
| **Entonces** | Se aplica 8% a ELECTRONICS y 12% a CLOTHING, acumulándose con otros descuentos |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-ORD-002
- **Épica:** EP-ORD-002
- **Dependencias:** US-ORD-001, US-ORD-007
- **Versión/Release:** 1.1

---

### US-ORD-010: Aplicar descuento estacional

**Descripción:**  
Como sistema de marketing, Quiero aplicar descuentos por temporada, Para maximizar las ventas durante épocas especiales del año.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Una orden creada en época WINTER |
| **Cuando** | Se crea la orden |
| **Entonces** | Se aplica descuento del 15% acumulable con otros beneficios activos |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-ORD-002
- **Épica:** EP-ORD-002
- **Dependencias:** US-ORD-001, US-ORD-007
- **Versión/Release:** 1.1

---

### US-ORD-011: Combinar múltiples descuentos

**Descripción:**  
Como motor de precios, Quiero aplicar múltiples capas de descuentos concurrentes, Para calcular el precio final exacto según el perfil del cliente y entorno.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un cliente GOLD comprando ELECTRONICS en WINTER |
| **Cuando** | Se crea la orden |
| **Entonces** | Se aplican todos los descuentos acumulativamente (15%, 8%, 15%) y el totalAfterDiscount es correcto |

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-ORD-002
- **Épica:** EP-ORD-002
- **Dependencias:** US-ORD-008, US-ORD-009, US-ORD-010
- **Versión/Release:** 1.1

---

## Feature FT-ORD-003 - Gestión de Estados de Órdenes

### US-ORD-012: Cambiar estado válido

**Descripción:**  
Como gestor de pedidos, Quiero actualizar el estado de una orden a pagada, Para reflejar el progreso del ciclo de vida del pedido.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Una orden en estado CREATED |
| **Cuando** | Se invoca PATCH /api/orders/{id}/status con newStatus = PAID |
| **Entonces** | La orden cambia a PAID y updatedAt se actualiza automáticamente |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-ORD-003 - Gestión de Estados de Órdenes
- **Épica:** EP-ORD-001
- **Dependencias:** US-ORD-001
- **Versión/Release:** 1.0

---

### US-ORD-013: Validar transiciones de estado

**Descripción:**  
Como motor de flujo de trabajo, Quiero restringir las transiciones de estado no permitidas, Para mantener la integridad del proceso de negocio.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Una orden en estado CREATED |
| **Cuando** | Se intenta cambiar a estado COMPLETED sin pasos previos |
| **Entonces** | Se rechaza el cambio y la orden mantiene su estado actual |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-ORD-003
- **Épica:** EP-ORD-001
- **Dependencias:** US-ORD-012
- **Versión/Release:** 1.0

---

### US-ORD-014: Estado final terminal

**Descripción:**  
Como motor de flujo de trabajo, Quiero asegurar que los estados terminales no sean modificables, Para evitar inconsistencias en órdenes ya finalizadas.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Una orden en estado COMPLETED |
| **Cuando** | Se intenta cambiar de estado |
| **Entonces** | Se rechaza la solicitud dado que COMPLETED es un estado terminal |

**Metadatos:**
- **Prioridad:** Baja
- **Feature:** FT-ORD-003
- **Épica:** EP-ORD-001
- **Dependencias:** US-ORD-012
- **Versión/Release:** 1.0

---

## Feature FT-ORD-004 - Consulta y Recuperación de Órdenes

### US-ORD-015: Recuperar orden por ID

**Descripción:**  
Como usuario del sistema, Quiero consultar los detalles de una orden específica por su ID, Para visualizar la información completa de la compra.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un orderId válido que existe en el sistema |
| **Cuando** | Se invoca GET /api/orders/{id} |
| **Entonces** | Se retorna la orden con todos sus items y código HTTP 200 |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-ORD-004 - Consulta y Recuperación de Órdenes
- **Épica:** EP-ORD-001
- **Dependencias:** US-ORD-001
- **Versión/Release:** 1.0

---

### US-ORD-016: Recuperar orden inexistente

**Descripción:**  
Como usuario del sistema, Quiero recibir una notificación cuando una orden no sea encontrada, Para saber que el ID proporcionado es erróneo.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un orderId que no existe |
| **Cuando** | Se invoca GET /api/orders/{id} |
| **Entonces** | Se retorna código HTTP 404 Not Found |

**Metadatos:**
- **Prioridad:** Baja
- **Feature:** FT-ORD-004
- **Épica:** EP-ORD-001
- **Dependencias:** US-ORD-015
- **Versión/Release:** 1.0

---

### US-ORD-017: Listar órdenes por cliente

**Descripción:**  
Como cliente o administrador, Quiero listar todas las órdenes asociadas a un cliente específico, Para realizar un seguimiento del historial de compras.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un customerId con múltiples órdenes registradas |
| **Cuando** | Se recuperan órdenes por cliente |
| **Entonces** | Se retorna la lista completa de órdenes asociadas |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-ORD-004
- **Épica:** EP-ORD-001
- **Dependencias:** US-ORD-015
- **Versión/Release:** 1.0

---

## Feature FT-ORD-005 - Seguridad y Autenticación

### US-ORD-018: Autenticar con JWT Token

**Descripción:**  
Como sistema de seguridad, Quiero validar la identidad de los usuarios mediante tokens JWT, Para proteger el acceso a los recursos del sistema.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un JWT token válido en el header Authorization |
| **Cuando** | Se invoca un endpoint protegido |
| **Entonces** | El filtro valida el token y se establece el contexto de seguridad del usuario |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-ORD-005 - Seguridad y Autenticación
- **Épica:** EP-SEC-001 - Seguridad del Sistema
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

---

### US-ORD-019: Rechazar token inválido

**Descripción:**  
Como sistema de seguridad, Quiero denegar el acceso a usuarios con tokens no válidos, Para prevenir accesos no autorizados.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un JWT token expirado o inválido |
| **Cuando** | Se invoca un endpoint protegido |
| **Entonces** | Se rechaza la solicitud y no se procesa ninguna operación |

**Metadatos:**
- **Prioridad:** Muy Alta
- **Feature:** FT-ORD-005
- **Épica:** EP-SEC-001
- **Dependencias:** US-ORD-018
- **Versión/Release:** 1.0

---

### US-ORD-020: Permitir acceso público a ciertos endpoints

**Descripción:**  
Como administrador de red, Quiero exceptuar ciertos endpoints de la validación de seguridad, Para permitir el acceso a documentación y salud del sistema.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Endpoints públicos como /api/orders/, /swagger-ui/ o /actuator/health |
| **Cuando** | Se invocan sin autenticación |
| **Entonces** | Se permiten las solicitudes sin requerir token JWT |

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-ORD-005
- **Épica:** EP-SEC-001
- **Dependencias:** US-ORD-018
- **Versión/Release:** 1.0

---

## 📊 Matriz de Cobertura: Tests vs Historias de Usuario

| US | Título | Test Unitario | Test Integración | ✅ |
|---|---|---|---|---|
| US-ORD-001 | Crear orden válida | OrderServiceTest | OrderRestControllerTest | ✅ |
| US-ORD-002 | Cliente inactivo | OrderServiceTest | N/A | ✅ |
| US-ORD-003 | Cliente bloqueado | OrderServiceTest | N/A | ✅ |
| US-ORD-004 | Cliente inexistente | OrderServiceTest | N/A | ✅ |
| US-ORD-005 | Stock insuficiente | OrderServiceTest | N/A | ✅ |
| US-ORD-006 | Producto inválido | OrderServiceTest | N/A | ✅ |
| US-ORD-007 | Calcular totales | MoneyTest, OrderTest | N/A | ✅ |
| US-ORD-008 | Descuento fidelidad GOLD | LoyaltyDiscountStrategyTest | OrderServiceTest | ✅ |
| US-ORD-009 | Descuento por tipo | ProductTypeDiscountStrategyTest | OrderServiceTest | ✅ |
| US-ORD-010 | Descuento estacional | SeasonDiscountStrategyTest | OrderServiceTest | ✅ |
| US-ORD-011 | Múltiples descuentos | OrderServiceTest | N/A | ✅ |
| US-ORD-012 | Cambiar estado válido | OrderStatusTest, OrderTest | OrderRestControllerTest | ✅ |
| US-ORD-013 | Validar transiciones | OrderStatusTest | N/A | ✅ |
| US-ORD-014 | Estado terminal | OrderStatusTest | N/A | ✅ |
| US-ORD-015 | Recuperar orden | OrderPersistenceAdapterTest | OrderRestControllerTest | ✅ |
| US-ORD-016 | Orden no existe | OrderRestControllerTest | N/A | ✅ |
| US-ORD-017 | Órdenes por cliente | OrderPersistenceAdapterTest | N/A | ✅ |
| US-ORD-018 | Autenticar JWT | JwtTokenValidatorTest | JwtAuthenticationFilterTest | ✅ |
| US-ORD-019 | Token inválido | JwtTokenValidatorTest | N/A | ✅ |
| US-ORD-020 | Endpoints públicos | SecurityConfigTest | N/A | ✅ |

---

## 📐 Resumen INVEST

| Principio | Cumplimiento | Observación |
|---|---|---|
| **I**ndependent | ✅✅✅ | Dependencias explícitas, Features aisladas, desarrollo paralelo |
| **N**egotiable | ✅✅ | Sin detalles técnicos prescriptivos, enfoque en valor |
| **V**aluable | ✅✅✅ | Cada HU tiene "Para" claro, 5 actores diferentes |
| **E**stimable | ✅✅✅ | Criterios concretos, acotados, cuantificables |
| **S**mall | ✅✅✅ | Sprint-sized (2-5 puntos cada una), 20 HU ÷ 5 sprints |
| **T**estable | ✅✅✅ | Escenarios específicos, "Entonces" verificables |

---

## 🏗️ Notas Arquitectónicas

- **Domain Layer**: Money, Order, OrderItem, OrderStatus validan reglas de negocio puras
- **Strategies Pattern**: Tres estrategias independientes de descuento (Loyalty, ProductType, Seasonal) aplicables simultáneamente
- **Stateless REST API**: Endpoints bajo `/api/orders` sin sesiones, autenticación vía JWT
- **State Machine**: OrderStatus valida transiciones (CREATED→PAID→SHIPPED→COMPLETED)
- **Ports & Adapters**: Abstracción de BD (OrderPersistencePort), validación de clientes (CustomerValidationPort) y productos (ProductValidationPort)