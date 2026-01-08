package com.novacommerce.order_service.repository.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para OrderItemEntity.
 * Valida el comportamiento de la entidad JPA de items de orden.
 */
class OrderItemEntityTest {

    @Test
    void givenNewOrderItemEntity_whenBuild_thenCreatesCorrectly() {
        // GIVEN/WHEN - Construir OrderItemEntity
        OrderItemEntity item = OrderItemEntity.builder()
                .id(1L)
                .productId(100L)
                .productName("Laptop Dell XPS")
                .quantity(2)
                .unitPrice(new BigDecimal("1200.50"))
                .productType("ELECTRONICS")
                .build();

        // THEN - Debe tener los valores correctos
        assertEquals(1L, item.getId());
        assertEquals(100L, item.getProductId());
        assertEquals("Laptop Dell XPS", item.getProductName());
        assertEquals(2, item.getQuantity());
        assertEquals(0, new BigDecimal("1200.50").compareTo(item.getUnitPrice()));
        assertEquals("ELECTRONICS", item.getProductType());
    }

    @Test
    void givenOrderItemEntity_whenSetOrder_thenOrderIsSet() {
        // GIVEN - OrderItemEntity y OrderEntity
        OrderItemEntity item = OrderItemEntity.builder()
                .id(2L)
                .productId(200L)
                .productName("Mouse")
                .quantity(1)
                .unitPrice(new BigDecimal("25.00"))
                .build();

        OrderEntity order = OrderEntity.builder()
                .id(10L)
                .customerId(50L)
                .build();

        // WHEN - Establecer la orden
        item.setOrder(order);

        // THEN - La orden debe estar establecida
        assertEquals(order, item.getOrder());
        assertEquals(10L, item.getOrder().getId());
    }

    @Test
    void givenOrderItemEntity_whenGetOrder_thenReturnsOrder() {
        // GIVEN - OrderEntity con item
        OrderEntity order = OrderEntity.builder()
                .id(15L)
                .customerId(75L)
                .build();

        OrderItemEntity item = OrderItemEntity.builder()
                .id(3L)
                .productId(300L)
                .productName("Keyboard")
                .quantity(1)
                .unitPrice(new BigDecimal("50.00"))
                .order(order)
                .build();

        // WHEN - Obtener la orden
        OrderEntity retrievedOrder = item.getOrder();

        // THEN - Debe retornar la orden correcta
        assertNotNull(retrievedOrder);
        assertEquals(15L, retrievedOrder.getId());
        assertEquals(75L, retrievedOrder.getCustomerId());
    }

    @Test
    void givenOrderItemEntity_whenChangeQuantity_thenQuantityIsUpdated() {
        // GIVEN - OrderItemEntity con cantidad inicial
        OrderItemEntity item = OrderItemEntity.builder()
                .id(4L)
                .productId(400L)
                .productName("Monitor")
                .quantity(1)
                .unitPrice(new BigDecimal("300.00"))
                .build();

        // WHEN - Cambiar cantidad
        item.setQuantity(3);

        // THEN - Cantidad debe actualizarse
        assertEquals(3, item.getQuantity());
    }

    @Test
    void givenOrderItemEntity_whenChangeUnitPrice_thenPriceIsUpdated() {
        // GIVEN - OrderItemEntity con precio inicial
        OrderItemEntity item = OrderItemEntity.builder()
                .id(5L)
                .productId(500L)
                .productName("Headphones")
                .quantity(2)
                .unitPrice(new BigDecimal("75.00"))
                .build();

        // WHEN - Cambiar precio unitario
        BigDecimal newPrice = new BigDecimal("85.00");
        item.setUnitPrice(newPrice);

        // THEN - Precio debe actualizarse
        assertEquals(0, newPrice.compareTo(item.getUnitPrice()));
    }

    @Test
    void givenOrderItemEntityWithoutProductType_whenBuild_thenProductTypeIsNull() {
        // GIVEN/WHEN - Construir OrderItemEntity sin productType
        OrderItemEntity item = OrderItemEntity.builder()
                .id(6L)
                .productId(600L)
                .productName("Generic Product")
                .quantity(1)
                .unitPrice(new BigDecimal("10.00"))
                .build();

        // THEN - ProductType debe ser null
        assertNull(item.getProductType());
    }

    @Test
    void givenOrderItemEntity_whenSetProductType_thenProductTypeIsSet() {
        // GIVEN - OrderItemEntity sin productType
        OrderItemEntity item = OrderItemEntity.builder()
                .id(7L)
                .productId(700L)
                .productName("T-Shirt")
                .quantity(3)
                .unitPrice(new BigDecimal("20.00"))
                .build();

        // WHEN - Establecer productType
        item.setProductType("CLOTHING");

        // THEN - ProductType debe estar establecido
        assertEquals("CLOTHING", item.getProductType());
    }

    @Test
    void givenTwoOrderItemEntities_whenCompare_thenEqualsAndHashCodeWork() {
        // GIVEN - Dos OrderItemEntity con mismo ID y valores
        OrderItemEntity item1 = OrderItemEntity.builder()
                .id(20L)
                .productId(800L)
                .productName("Cable USB")
                .quantity(5)
                .unitPrice(new BigDecimal("5.00"))
                .productType("ACCESSORIES")
                .build();

        OrderItemEntity item2 = OrderItemEntity.builder()
                .id(20L)
                .productId(800L)
                .productName("Cable USB")
                .quantity(5)
                .unitPrice(new BigDecimal("5.00"))
                .productType("ACCESSORIES")
                .build();

        // THEN - Deben ser iguales (Lombok @Data genera equals/hashCode)
        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    void givenOrderItemEntity_whenUseNoArgsConstructor_thenCanSetFields() {
        // GIVEN/WHEN - Crear con constructor sin argumentos
        OrderItemEntity item = new OrderItemEntity();
        item.setId(8L);
        item.setProductId(900L);
        item.setProductName("Webcam");
        item.setQuantity(1);
        item.setUnitPrice(new BigDecimal("60.00"));
        item.setProductType("ELECTRONICS");

        // THEN - Todos los campos deben estar establecidos
        assertEquals(8L, item.getId());
        assertEquals(900L, item.getProductId());
        assertEquals("Webcam", item.getProductName());
        assertEquals(1, item.getQuantity());
        assertEquals(0, new BigDecimal("60.00").compareTo(item.getUnitPrice()));
        assertEquals("ELECTRONICS", item.getProductType());
    }

    @Test
    void givenOrderItemEntity_whenUseAllArgsConstructor_thenAllFieldsAreSet() {
        // GIVEN - OrderEntity para la relación
        OrderEntity order = OrderEntity.builder()
                .id(25L)
                .customerId(100L)
                .build();

        // WHEN - Crear con constructor de todos los argumentos
        OrderItemEntity item = new OrderItemEntity(
                9L,
                order,
                1000L,
                "Smartphone",
                1,
                new BigDecimal("799.99"),
                "ELECTRONICS"
        );

        // THEN - Todos los campos deben estar establecidos
        assertEquals(9L, item.getId());
        assertEquals(order, item.getOrder());
        assertEquals(1000L, item.getProductId());
        assertEquals("Smartphone", item.getProductName());
        assertEquals(1, item.getQuantity());
        assertEquals(0, new BigDecimal("799.99").compareTo(item.getUnitPrice()));
        assertEquals("ELECTRONICS", item.getProductType());
    }
}
