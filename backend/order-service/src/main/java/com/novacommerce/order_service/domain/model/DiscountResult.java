package com.novacommerce.order_service.domain.model;

import lombok.Builder;
import lombok.Value;

/**
 * Resultado de aplicar una estrategia de descuento.
 */
@Value
@Builder
public class DiscountResult {
    Money discountAmount;
    String description;
    String strategyName;

    public static DiscountResult noDiscount(String strategyName) {
        return DiscountResult.builder()
                .discountAmount(Money.zero())
                .description("No discount applied")
                .strategyName(strategyName)
                .build();
    }

    public boolean hasDiscount() {
        return discountAmount != null && !discountAmount.isZero();
    }
}
