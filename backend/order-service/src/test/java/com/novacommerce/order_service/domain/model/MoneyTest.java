package com.novacommerce.order_service.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    @DisplayName("GIVEN valid amounts WHEN creating money THEN scales to 2 decimals and stores value")
    void createMoneyAndScale() {
        Money m1 = Money.of(10);
        Money m2 = Money.of(new BigDecimal("20.345"));
        assertEquals("10.00", m1.toString());
        assertEquals("20.35", m2.toString());
    }

    @Test
    @DisplayName("GIVEN negative amount WHEN creating money THEN throws")
    void negativeAmountThrows() {
        assertThrows(IllegalArgumentException.class, () -> Money.of(new BigDecimal("-1")));
    }

    @Test
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
}
