
# Historias de Usuario - Product Service (NovaCommerce)

## 📋 Épica Global
**EP-GLOBAL-001** - Plataforma de E-commerce Modular y Escalable

## 📦 Épica Product Service
**EP-PROD-001** - Gestión de Catálogo de Productos y Categorías

---

## Feature FT-PROD-001 - Creación y Validación de Productos

### US-PROD-001: Crear producto válido con datos completos

**Descripción:**  
Como administrador del catálogo, Quiero crear un nuevo producto con todos los datos requeridos, Para agregar artículos al inventario disponible para la venta.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario con rol ADMIN y datos válidos de producto (nombre, descripción, precio, tipo, categoría, stock) |
| **Cuando** | Se invoca POST /api/products con datos completos y válidos |
| **Entonces** | El producto se crea correctamente, se persiste en la base de datos, y retorna HTTP 201 con los datos del producto creado |

**Criterios de Inversión (INVEST):**
- **I (Independent):** No depende del estado previo de otros productos
- **N (Negotiable):** Los detalles de validación pueden refinarse
- **V (Valuable):** Permite agregar artículos al catálogo
- **E (Estimable):** Se puede estimar el esfuerzo
- **S (Small):** Se completa en un sprint
- **T (Testeable):** Validable mediante tests unitarios y de integración

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-PROD-001 - Creación y Validación de Productos
- **Épica:** EP-PROD-001 - Gestión de Catálogo
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

---

### US-PROD-002: Rechazar producto sin nombre

**Descripción:**  
Como sistema de validación, Quiero asegurar que todo producto tenga un nombre, Para garantizar la integridad del catálogo.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario ADMIN intenta crear un producto sin nombre |
| **Cuando** | Se invoca POST /api/products sin el campo "name" |
| **Entonces** | Se rechaza la solicitud con HTTP 400 Bad Request y mensaje de validación |

**Criterios de Inversión (INVEST):**
- **I (Independent):** Validación aislada
- **N (Negotiable):** Puede ajustarse el mensaje de error
- **V (Valuable):** Previene datos inconsistentes
- **E (Estimable):** Trivial de implementar
- **S (Small):** Rápido de completar
- **T (Testeable):** Fácilmente testeable

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-PROD-001
- **Épica:** EP-PROD-001
- **Dependencias:** US-PROD-001
- **Versión/Release:** 1.0

---

### US-PROD-003: Rechazar producto con precio negativo

**Descripción:**  
Como sistema de validación, Quiero asegurar que el precio sea válido, Para evitar transacciones incorrectas.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario ADMIN intenta crear un producto con precio negativo |
| **Cuando** | Se invoca POST /api/products con price < 0 |
| **Entonces** | Se rechaza con HTTP 400 Bad Request y mensaje de validación |

**Criterios de Inversión (INVEST):**
- **I (Independent):** Validación independiente
- **N (Negotiable):** Rango de precios negociable
- **V (Valuable):** Protege la integridad de precios
- **E (Estimable):** Fácil de estimar
- **S (Small):** Muy pequeño
- **T (Testeable):** Claramente testeable

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-PROD-001
- **Épica:** EP-PROD-001
- **Dependencias:** US-PROD-001
- **Versión/Release:** 1.0

---

### US-PROD-004: Rechazar producto con stock negativo

**Descripción:**  
Como sistema de inventario, Quiero asegurar que la cantidad de stock sea positiva, Para mantener la coherencia del inventario.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario ADMIN intenta crear un producto con stock negativo |
| **Cuando** | Se invoca POST /api/products con stockQuantity < 0 |
| **Entonces** | Se rechaza con HTTP 400 Bad Request y mensaje de validación |

**Criterios de Inversión (INVEST):**
- **I (Independent):** Validación aislada
- **N (Negotiable):** Puede permitirse stock cero
- **V (Valuable):** Evita inconsistencias de inventario
- **E (Estimable):** Simple de estimar
- **S (Small):** Rápido de implementar
- **T (Testeable):** Fácilmente testeable

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-PROD-001
- **Épica:** EP-PROD-001
- **Dependencias:** US-PROD-001
- **Versión/Release:** 1.0

---

### US-PROD-005: Rechazar producto con categoría inválida

**Descripción:**  
Como sistema de catálogo, Quiero validar que la categoría exista, Para asegurar que los productos estén correctamente clasificados.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario ADMIN intenta crear un producto con categoryId que no existe |
| **Cuando** | Se invoca POST /api/products con categoryId inválido |
| **Entonces** | Se rechaza con HTTP 400 Bad Request indicando categoría no válida |

**Criterios de Inversión (INVEST):**
- **I (Independent):** Validación independiente
- **N (Negotiable):** Detalles del mensaje negociables
- **V (Valuable):** Mantiene integridad referencial
- **E (Estimable):** Estimable
- **S (Small):** Pequeño
- **T (Testeable):** Testeable

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-PROD-001
- **Épica:** EP-PROD-001
- **Dependencias:** US-PROD-001
- **Versión/Release:** 1.0

---

### US-PROD-006: Rechazar producto con tipo inválido

**Descripción:**  
Como sistema de validación, Quiero asegurar que el tipo de producto sea válido, Para mantener la consistencia del catálogo.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario ADMIN intenta crear un producto con productType inválido |
| **Cuando** | Se invoca POST /api/products con productType no perteneciente a [PHYSICAL, DIGITAL, SERVICE, SUBSCRIPTION] |
| **Entonces** | Se rechaza con HTTP 400 Bad Request indicando tipo de producto inválido |

**Criterios de Inversión (INVEST):**
- **I (Independent):** Validación aislada
- **N (Negotiable):** Tipos de producto negociables
- **V (Valuable):** Asegura categorización correcta
- **E (Estimable):** Fácil de estimar
- **S (Small):** Muy pequeño
- **T (Testeable):** Trivial testeable

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-PROD-001
- **Épica:** EP-PROD-001
- **Dependencias:** US-PROD-001
- **Versión/Release:** 1.0

---

## Feature FT-PROD-002 - Consulta y Recuperación de Productos

### US-PROD-007: Obtener todos los productos con paginación

**Descripción:**  
Como usuario del catálogo, Quiero recuperar la lista de productos con paginación, Para navegar eficientemente por el catálogo.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Existe un conjunto de productos activos en el sistema |
| **Cuando** | Se invoca GET /api/products?page=0&size=20 |
| **Entonces** | Retorna HTTP 200 con lista paginada de productos, metadatos de paginación (totalElements, totalPages, currentPage) |

**Criterios de Inversión (INVEST):**
- **I (Independent):** No afecta a otras operaciones
- **N (Negotiable):** Tamaño de página negociable
- **V (Valuable):** Mejora la experiencia del usuario
- **E (Estimable):** Bien definido
- **S (Small):** Conjunto pequeño
- **T (Testeable):** Fácilmente testeable

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-PROD-002 - Consulta y Recuperación de Productos
- **Épica:** EP-PROD-001
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

---

### US-PROD-008: Obtener producto por ID

**Descripción:**  
Como usuario del catálogo, Quiero recuperar los detalles de un producto específico, Para revisar sus características completas.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un producto con ID válido existe en el sistema |
| **Cuando** | Se invoca GET /api/products/{id} |
| **Entonces** | Retorna HTTP 200 con los detalles completos del producto |
| **Y (Escenario alternativo)** | Si el producto no existe, retorna HTTP 404 Not Found |

**Criterios de Inversión (INVEST):**
- **I (Independent):** Operación aislada
- **N (Negotiable):** Campos incluibles negociables
- **V (Valuable):** Acceso a detalles esencial
- **E (Estimable):** Bien definido
- **S (Small):** Operación simple
- **T (Testeable):** Completamente testeable

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-PROD-002
- **Épica:** EP-PROD-001
- **Dependencias:** US-PROD-001
- **Versión/Release:** 1.0

---

### US-PROD-009: Obtener productos por categoría

**Descripción:**  
Como usuario del catálogo, Quiero filtrar productos por categoría, Para encontrar artículos de mi interés.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Existe una categoría con productos asociados |
| **Cuando** | Se invoca GET /api/products/category/{categoryId} |
| **Entonces** | Retorna HTTP 200 con lista paginada de productos de esa categoría |

**Criterios de Inversión (INVEST):**
- **I (Independent):** Filtrado independiente
- **N (Negotiable):** Criterios de filtrado negociables
- **V (Valuable):** Navegación mejorada
- **E (Estimable):** Estimable
- **S (Small):** Relativamente pequeño
- **T (Testeable):** Testeable

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-PROD-002
- **Épica:** EP-PROD-001
- **Dependencias:** US-PROD-007
- **Versión/Release:** 1.0

---

## Feature FT-PROD-003 - Actualización de Productos

### US-PROD-010: Actualizar producto existente

**Descripción:**  
Como administrador del catálogo, Quiero actualizar los datos de un producto existente, Para mantener la información del catálogo actualizada.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario ADMIN y un producto existente con ID válido |
| **Cuando** | Se invoca PUT /api/products/{id} con datos actualizados |
| **Entonces** | El producto se actualiza en la base de datos y retorna HTTP 200 con los datos actualizados |

**Criterios de Inversión (INVEST):**
- **I (Independent):** Actualización aislada
- **N (Negotiable):** Campos actualizables negociables
- **V (Valuable):** Mantenimiento del catálogo
- **E (Estimable):** Bien definido
- **S (Small):** Operación simple
- **T (Testeable):** Fácilmente testeable

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-PROD-003 - Actualización de Productos
- **Épica:** EP-PROD-001
- **Dependencias:** US-PROD-001, US-PROD-008
- **Versión/Release:** 1.0

---

### US-PROD-011: Rechazar actualización de producto inexistente

**Descripción:**  
Como sistema de validación, Quiero rechazar la actualización de productos que no existen, Para evitar crear datos huérfanos.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario ADMIN intenta actualizar un producto con ID que no existe |
| **Cuando** | Se invoca PUT /api/products/{invalidId} |
| **Entonces** | Se rechaza con HTTP 404 Not Found |

**Criterios de Inversión (INVEST):**
- **I (Independent):** Validación aislada
- **N (Negotiable):** Mensaje de error negociable
- **V (Valuable):** Previene errores
- **E (Estimable):** Simple
- **S (Small):** Muy pequeño
- **T (Testeable):** Trivial testeable

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-PROD-003
- **Épica:** EP-PROD-001
- **Dependencias:** US-PROD-010
- **Versión/Release:** 1.0

---

## Feature FT-PROD-004 - Eliminación de Productos

### US-PROD-012: Eliminar producto existente

**Descripción:**  
Como administrador del catálogo, Quiero eliminar un producto del sistema, Para remover artículos del catálogo.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario ADMIN y un producto existente con ID válido |
| **Cuando** | Se invoca DELETE /api/products/{id} |
| **Entonces** | El producto se elimina de la base de datos y retorna HTTP 204 No Content |

**Criterios de Inversión (INVEST):**
- **I (Independent):** Operación aislada
- **N (Negotiable):** Política de eliminación negociable
- **V (Valuable):** Mantenimiento del catálogo
- **E (Estimable):** Bien definido
- **S (Small):** Pequeño
- **T (Testeable):** Fácilmente testeable

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-PROD-004 - Eliminación de Productos
- **Épica:** EP-PROD-001
- **Dependencias:** US-PROD-008
- **Versión/Release:** 1.0

---

## Feature FT-PROD-005 - Gestión de Categorías

### US-PROD-013: Crear categoría válida

**Descripción:**  
Como administrador del catálogo, Quiero crear una nueva categoría de productos, Para organizar el catálogo de manera jerárquica.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario ADMIN con datos válidos de categoría (nombre, descripción) |
| **Cuando** | Se invoca POST /api/categories con datos completos |
| **Entonces** | La categoría se crea correctamente y retorna HTTP 201 con los datos de la categoría creada |

**Criterios de Inversión (INVEST):**
- **I (Independent):** Creación aislada
- **N (Negotiable):** Estructura de categoría negociable
- **V (Valuable):** Organización del catálogo
- **E (Estimable):** Bien definido
- **S (Small):** Pequeño
- **T (Testeable):** Fácilmente testeable

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-PROD-005 - Gestión de Categorías
- **Épica:** EP-PROD-001
- **Dependencias:** Ninguna
- **Versión/Release:** 1.0

---

### US-PROD-014: Rechazar categoría sin nombre

**Descripción:**  
Como sistema de validación, Quiero asegurar que toda categoría tenga un nombre, Para garantizar la integridad del catálogo.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario ADMIN intenta crear una categoría sin nombre |
| **Cuando** | Se invoca POST /api/categories sin el campo "name" |
| **Entonces** | Se rechaza con HTTP 400 Bad Request |

**Criterios de Inversión (INVEST):**
- **I (Independent):** Validación aislada
- **N (Negotiable):** Mensaje de error negociable
- **V (Valuable):** Integridad de datos
- **E (Estimable):** Simple
- **S (Small):** Muy pequeño
- **T (Testeable):** Trivial testeable

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-PROD-005
- **Épica:** EP-PROD-001
- **Dependencias:** US-PROD-013
- **Versión/Release:** 1.0

---

### US-PROD-015: Obtener todas las categorías

**Descripción:**  
Como usuario del catálogo, Quiero recuperar la lista de todas las categorías, Para navegar la estructura del catálogo.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Existen categorías en el sistema |
| **Cuando** | Se invoca GET /api/categories |
| **Entonces** | Retorna HTTP 200 con lista paginada de categorías |

**Criterios de Inversión (INVEST):**
- **I (Independent):** Consulta aislada
- **N (Negotiable):** Paginación negociable
- **V (Valuable):** Navegación esencial
- **E (Estimable):** Bien definido
- **S (Small):** Pequeño
- **T (Testeable):** Fácilmente testeable

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-PROD-005
- **Épica:** EP-PROD-001
- **Dependencias:** US-PROD-013
- **Versión/Release:** 1.0

---

### US-PROD-016: Obtener categoría por ID

**Descripción:**  
Como usuario del catálogo, Quiero recuperar los detalles de una categoría específica, Para revisar su descripción y propiedades.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Una categoría con ID válido existe en el sistema |
| **Cuando** | Se invoca GET /api/categories/{id} |
| **Entonces** | Retorna HTTP 200 con los detalles de la categoría |
| **Y (Escenario alternativo)** | Si no existe, retorna HTTP 404 Not Found |

**Criterios de Inversión (INVEST):**
- **I (Independent):** Consulta aislada
- **N (Negotiable):** Campos incluibles negociables
- **V (Valuable):** Acceso a detalles
- **E (Estimable):** Bien definido
- **S (Small):** Pequeño
- **T (Testeable):** Fácilmente testeable

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-PROD-005
- **Épica:** EP-PROD-001
- **Dependencias:** US-PROD-015
- **Versión/Release:** 1.0

---

### US-PROD-017: Actualizar categoría existente

**Descripción:**  
Como administrador del catálogo, Quiero actualizar los datos de una categoría, Para mantener la información actualizada.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario ADMIN y una categoría existente con ID válido |
| **Cuando** | Se invoca PUT /api/categories/{id} con datos actualizados |
| **Entonces** | La categoría se actualiza y retorna HTTP 200 con los datos actualizados |

**Criterios de Inversión (INVEST):**
- **I (Independent):** Actualización aislada
- **N (Negotiable):** Campos actualizables negociables
- **V (Valuable):** Mantenimiento
- **E (Estimable):** Bien definido
- **S (Small):** Pequeño
- **T (Testeable):** Fácilmente testeable

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-PROD-005
- **Épica:** EP-PROD-001
- **Dependencias:** US-PROD-013, US-PROD-016
- **Versión/Release:** 1.0

---

### US-PROD-018: Eliminar categoría existente

**Descripción:**  
Como administrador del catálogo, Quiero eliminar una categoría, Para remover clasificaciones no utilizadas.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un usuario ADMIN y una categoría existente con ID válido |
| **Cuando** | Se invoca DELETE /api/categories/{id} |
| **Entonces** | La categoría se elimina y retorna HTTP 204 No Content |

**Criterios de Inversión (INVEST):**
- **I (Independent):** Operación aislada
- **N (Negotiable):** Política de eliminación negociable
- **V (Valuable):** Mantenimiento
- **E (Estimable):** Bien definido
- **S (Small):** Pequeño
- **T (Testeable):** Fácilmente testeable

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-PROD-005
- **Épica:** EP-PROD-001
- **Dependencias:** US-PROD-016
- **Versión/Release:** 1.0

---

## Feature FT-PROD-006 - APIs Internas y Servicios Especiales

### US-PROD-019: Obtener productos vía API interna

**Descripción:**  
Como servicio interno de NovaCommerce, Quiero acceder a los productos de forma directa sin autenticación JWT, Para consultar datos de productos de manera eficiente entre microservicios.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un servicio interno con API key válida |
| **Cuando** | Se invoca GET /internal/products/{id} con header X-Internal-API-Key válida |
| **Entonces** | Retorna HTTP 200 con los datos del producto en formato interno |

**Criterios de Inversión (INVEST):**
- **I (Independent):** API aislada
- **N (Negotiable):** Formato de respuesta negociable
- **V (Valuable):** Comunicación interservicios
- **E (Estimable):** Bien definido
- **S (Small):** Pequeño
- **T (Testeable):** Fácilmente testeable

**Metadatos:**
- **Prioridad:** Media
- **Feature:** FT-PROD-006 - APIs Internas y Servicios Especiales
- **Épica:** EP-PROD-001
- **Dependencias:** US-PROD-008
- **Versión/Release:** 1.0

---

### US-PROD-020: Rechazar acceso a API interna sin API key

**Descripción:**  
Como sistema de seguridad, Quiero validar que las APIs internas incluyan una API key válida, Para proteger los datos internos.

**Criterios de Aceptación:**

| Escenario | Condición |
|-----------|-----------|
| **Dado** | Un cliente intenta acceder a /internal/products sin API key |
| **Cuando** | Se invoca GET /internal/products/{id} sin header X-Internal-API-Key |
| **Entonces** | Se rechaza con HTTP 401 Unauthorized |

**Criterios de Inversión (INVEST):**
- **I (Independent):** Validación aislada
- **N (Negotiable):** Mensaje de error negociable
- **V (Valuable):** Seguridad
- **E (Estimable):** Simple
- **S (Small):** Muy pequeño
- **T (Testeable):** Trivial testeable

**Metadatos:**
- **Prioridad:** Alta
- **Feature:** FT-PROD-006
- **Épica:** EP-PROD-001
- **Dependencias:** US-PROD-019
- **Versión/Release:** 1.0

---

## Resumen de Historias de Usuario

| ID | Nombre | Prioridad | Estado | Feature |
|----|--------|-----------|--------|---------|
| US-PROD-001 | Crear producto válido | Alta | Pendiente | FT-PROD-001 |
| US-PROD-002 | Rechazar producto sin nombre | Alta | Pendiente | FT-PROD-001 |
| US-PROD-003 | Rechazar precio negativo | Alta | Pendiente | FT-PROD-001 |
| US-PROD-004 | Rechazar stock negativo | Alta | Pendiente | FT-PROD-001 |
| US-PROD-005 | Rechazar categoría inválida | Alta | Pendiente | FT-PROD-001 |
| US-PROD-006 | Rechazar tipo inválido | Alta | Pendiente | FT-PROD-001 |
| US-PROD-007 | Obtener todos los productos | Alta | Pendiente | FT-PROD-002 |
| US-PROD-008 | Obtener producto por ID | Alta | Pendiente | FT-PROD-002 |
| US-PROD-009 | Obtener por categoría | Alta | Pendiente | FT-PROD-002 |
| US-PROD-010 | Actualizar producto | Media | Pendiente | FT-PROD-003 |
| US-PROD-011 | Rechazar actualización inexistente | Alta | Pendiente | FT-PROD-003 |
| US-PROD-012 | Eliminar producto | Media | Pendiente | FT-PROD-004 |
| US-PROD-013 | Crear categoría | Alta | Pendiente | FT-PROD-005 |
| US-PROD-014 | Rechazar categoría sin nombre | Alta | Pendiente | FT-PROD-005 |
| US-PROD-015 | Obtener todas categorías | Alta | Pendiente | FT-PROD-005 |
| US-PROD-016 | Obtener categoría por ID | Alta | Pendiente | FT-PROD-005 |
| US-PROD-017 | Actualizar categoría | Media | Pendiente | FT-PROD-005 |
| US-PROD-018 | Eliminar categoría | Media | Pendiente | FT-PROD-005 |
| US-PROD-019 | API interna productos | Media | Pendiente | FT-PROD-006 |
| US-PROD-020 | Validar API key interno | Alta | Pendiente | FT-PROD-006 |

---

## Notas sobre INVEST Criteria

Todas las historias de usuario en este documento siguen los principios INVEST:

- **Independent:** Cada HU es independiente y puede ser desarrollada en cualquier orden (respetando dependencias)
- **Negotiable:** Los detalles técnicos son negociables con el equipo de desarrollo
- **Valuable:** Cada HU proporciona valor real al negocio o a los usuarios
- **Estimable:** Todas las HUs son estimables por el equipo de desarrollo
- **Small:** Diseñadas para completarse en un sprint (típicamente 1-5 días)
- **Testeable:** Cada HU tiene criterios de aceptación claros y verificables

---

## Estado General del Proyecto

**Versión del Documento:** 1.0  
**Última Actualización:** 2026-01-08  
**Total de Historias de Usuario:** 20  
**Épicas:** 2  
**Features:** 6  

**Roadmap:**
- **Release 1.0:** Todas las funcionalidades básicas (US-PROD-001 a US-PROD-020)
- **Release 1.1:** Optimizaciones y features avanzadas (Por definir)

