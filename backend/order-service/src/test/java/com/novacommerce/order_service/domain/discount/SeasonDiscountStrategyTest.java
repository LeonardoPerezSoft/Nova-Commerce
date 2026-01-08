package com.novacommerce.order_service.domain.discount;

import com.novacommerce.order_service.domain.model.DiscountContext;
import com.novacommerce.order_service.domain.model.DiscountResult;
import com.novacommerce.order_service.domain.model.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeasonDiscountStrategyTest {

    private final SeasonDiscountStrategy strategy = new SeasonDiscountStrategy();

    @Test
    @DisplayName("GIVEN WINTER season WHEN apply THEN 15 percent discount")
    void winterDiscount() {
        DiscountContext ctx = DiscountContext.builder()
                .currentSeason("WINTER")
                .orderTotal(Money.of(100))
                .build();
        DiscountResult res = strategy.apply(ctx);
        assertTrue(res.hasDiscount());
        assertEquals("15.00", res.getDiscountAmount().toString());
    }

    @Test
    @DisplayName("GIVEN unknown season WHEN apply THEN no discount")
    void unknownSeason() {
        DiscountContext ctx = DiscountContext.builder()
                .currentSeason("UNKNOWN")
                .orderTotal(Money.of(100))
                .build();
        DiscountResult res = strategy.apply(ctx);
        assertFalse(res.hasDiscount());
    }
}
