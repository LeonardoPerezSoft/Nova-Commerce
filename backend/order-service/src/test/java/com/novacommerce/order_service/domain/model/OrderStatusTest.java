package com.novacommerce.order_service.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusTest {

    @Test
    @DisplayName("GIVEN status transitions WHEN valid THEN allowed")
    void validTransitions() {
        assertTrue(OrderStatus.CREATED.canTransitionTo(OrderStatus.PAID));
        assertTrue(OrderStatus.CREATED.canTransitionTo(OrderStatus.CANCELLED));
        assertTrue(OrderStatus.PAID.canTransitionTo(OrderStatus.SHIPPED));
        assertTrue(OrderStatus.PAID.canTransitionTo(OrderStatus.CANCELLED));
        assertTrue(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.COMPLETED));
    }

    @Test
    @DisplayName("GIVEN status transitions WHEN invalid THEN not allowed")
    void invalidTransitions() {
        assertFalse(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.CANCELLED));
        assertFalse(OrderStatus.COMPLETED.canTransitionTo(OrderStatus.PAID));
        assertFalse(OrderStatus.CANCELLED.canTransitionTo(OrderStatus.PAID));
    }

    @Test
    @DisplayName("GIVEN final states WHEN isFinal THEN true")
    void isFinal() {
        assertTrue(OrderStatus.CANCELLED.isFinal());
        assertTrue(OrderStatus.COMPLETED.isFinal());
        assertFalse(OrderStatus.CREATED.isFinal());
    }
}
