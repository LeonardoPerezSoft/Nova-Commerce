package com.novacommerce.order_service.adapter.out.persistence;

import com.novacommerce.order_service.domain.model.Money;
import com.novacommerce.order_service.domain.model.Order;
import com.novacommerce.order_service.domain.model.OrderItem;
import com.novacommerce.order_service.domain.model.OrderStatus;
import com.novacommerce.order_service.repository.entity.OrderEntity;
import com.novacommerce.order_service.repository.entity.OrderItemEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para OrderMapper.
 * Valida el mapeo bidireccional entre Order (dominio) y OrderEntity (JPA).
 */
class OrderMapperTest {

    private OrderMapper orderMapper;

    @BeforeEach
    void setUp() {
        orderMapper = new OrderMapper();
    }

    @Test
    void givenOrder_whenToEntity_thenMapsCorrectly() {
        // GIVEN - Orden de dominio con items
        OrderItem item1 = OrderItem.builder()
                .id(1L)
                .productId(100L)
                .productName("Laptop")
                .quantity(2)
                .unitPrice(Money.of(new BigDecimal("1000.00")))
                .productType("ELECTRONICS")
                .build();

        OrderItem item2 = OrderItem.builder()
                .id(2L)
                .productId(200L)
                .productName("Mouse")
                .quantity(5)
                .unitPrice(Money.of(new BigDecimal("20.00")))
                .productType("ELECTRONICS")
                .build();

        Order order = Order.builder()
                .id(1L)
                .customerId(42L)
                .status(OrderStatus.CREATED)
                .totalBeforeDiscount(Money.of(new BigDecimal("2100.00")))
                .discountTotal(Money.of(new BigDecimal("100.00")))
                .totalAfterDiscount(Money.of(new BigDecimal("2000.00")))
                .createdAt(LocalDateTime.of(2024, 1, 15, 10, 30))
                .updatedAt(LocalDateTime.of(2024, 1, 15, 10, 30))
                .items(Arrays.asList(item1, item2))
                .build();

        // WHEN - Convertir a entidad
        OrderEntity entity = orderMapper.toEntity(order);

        // THEN - Verificar campos de la orden
        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals(42L, entity.getCustomerId());
        assertEquals(OrderStatus.CREATED, entity.getStatus());
        assertEquals(0, new BigDecimal("2100.00").compareTo(entity.getTotalBeforeDiscount()));
        assertEquals(0, new BigDecimal("100.00").compareTo(entity.getDiscountTotal()));
        assertEquals(0, new BigDecimal("2000.00").compareTo(entity.getTotalAfterDiscount()));
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());

        // THEN - Verificar items
        assertEquals(2, entity.getItems().size());
        OrderItemEntity itemEntity1 = entity.getItems().get(0);
        assertEquals(1L, itemEntity1.getId());
        assertEquals(100L, itemEntity1.getProductId());
        assertEquals("Laptop", itemEntity1.getProductName());
        assertEquals(2, itemEntity1.getQuantity());
        assertEquals(0, new BigDecimal("1000.00").compareTo(itemEntity1.getUnitPrice()));
        assertEquals("ELECTRONICS", itemEntity1.getProductType());
    }

    @Test
    void givenOrderEntity_whenToDomain_thenMapsCorrectly() {
        // GIVEN - Entidad JPA con items
        OrderEntity entity = OrderEntity.builder()
                .id(5L)
                .customerId(99L)
                .status(OrderStatus.PAID)
                .totalBeforeDiscount(new BigDecimal("500.00"))
                .discountTotal(new BigDecimal("50.00"))
                .totalAfterDiscount(new BigDecimal("450.00"))
                .createdAt(LocalDateTime.of(2024, 2, 20, 14, 0))
                .updatedAt(LocalDateTime.of(2024, 2, 20, 15, 0))
                .build();

        OrderItemEntity itemEntity = OrderItemEntity.builder()
                .id(10L)
                .productId(300L)
                .productName("Keyboard")
                .quantity(3)
                .unitPrice(new BigDecimal("150.00"))
                .productType("ELECTRONICS")
                .build();

        entity.addItem(itemEntity);

        // WHEN - Convertir a dominio
        Order order = orderMapper.toDomain(entity);

        // THEN - Verificar campos de la orden
        assertNotNull(order);
        assertEquals(5L, order.getId());
        assertEquals(99L, order.getCustomerId());
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(0, new BigDecimal("500.00").compareTo(order.getTotalBeforeDiscountValue()));
        assertEquals(0, new BigDecimal("50.00").compareTo(order.getDiscountTotalValue()));
        assertEquals(0, new BigDecimal("450.00").compareTo(order.getTotalAfterDiscountValue()));
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());

        // THEN - Verificar items
        assertEquals(1, order.getItems().size());
        OrderItem item = order.getItems().get(0);
        assertEquals(10L, item.getId());
        assertEquals(300L, item.getProductId());
        assertEquals("Keyboard", item.getProductName());
        assertEquals(3, item.getQuantity());
        assertEquals(0, new BigDecimal("150.00").compareTo(item.getUnitPriceValue()));
        assertEquals("ELECTRONICS", item.getProductType());
    }

    @Test
    void givenNullOrder_whenToEntity_thenReturnsNull() {
        // WHEN - Convertir orden nula
        OrderEntity entity = orderMapper.toEntity(null);

        // THEN - Debe retornar null
        assertNull(entity);
    }

    @Test
    void givenNullOrderEntity_whenToDomain_thenReturnsNull() {
        // WHEN - Convertir entidad nula
        Order order = orderMapper.toDomain(null);

        // THEN - Debe retornar null
        assertNull(order);
    }

    @Test
    void givenOrderWithoutItems_whenToEntity_thenHandlesEmptyItems() {
        // GIVEN - Orden sin items
        Order order = Order.builder()
                .id(2L)
                .customerId(50L)
                .status(OrderStatus.CREATED)
                .totalBeforeDiscount(Money.of(BigDecimal.ZERO))
                .discountTotal(Money.of(BigDecimal.ZERO))
                .totalAfterDiscount(Money.of(BigDecimal.ZERO))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // WHEN - Convertir a entidad
        OrderEntity entity = orderMapper.toEntity(order);

        // THEN - Items debe estar vacío
        assertNotNull(entity);
        assertTrue(entity.getItems().isEmpty());
    }

    @Test
    void givenEntityWithoutItems_whenToDomain_thenHandlesEmptyItems() {
        // GIVEN - Entidad sin items
        OrderEntity entity = OrderEntity.builder()
                .id(3L)
                .customerId(60L)
                .status(OrderStatus.SHIPPED)
                .totalBeforeDiscount(new BigDecimal("100.00"))
                .discountTotal(BigDecimal.ZERO)
                .totalAfterDiscount(new BigDecimal("100.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // WHEN - Convertir a dominio
        Order order = orderMapper.toDomain(entity);

        // THEN - Items debe estar vacío
        assertNotNull(order);
        assertTrue(order.getItems().isEmpty());
    }
}
