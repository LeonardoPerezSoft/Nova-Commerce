package com.novacommerce.order_service.domain.discount;

import com.novacommerce.order_service.domain.model.DiscountContext;
import com.novacommerce.order_service.domain.model.DiscountResult;
import com.novacommerce.order_service.domain.model.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

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
        if (!isApplicable(context)) {
            return DiscountResult.noDiscount(getName());
        }

        BigDecimal percentage = getDiscountPercentage(context.getCustomerLoyaltyLevel());
        Money discountAmount = context.getOrderTotal().multiply(percentage);

        return DiscountResult.builder()
                .discountAmount(discountAmount)
                .description(String.format("Loyalty discount: %s (%s%%)", 
                        context.getCustomerLoyaltyLevel(), 
                        percentage.multiply(BigDecimal.valueOf(100))))
                .strategyName(getName())
                .build();
    }

    @Override
    public String getName() {
        return "LoyaltyDiscount";
    }

    @Override
    public boolean isApplicable(DiscountContext context) {
        return context.hasLoyaltyLevel() && context.isCustomerActive();
    }

    private BigDecimal getDiscountPercentage(String loyaltyLevel) {
        return switch (loyaltyLevel.toUpperCase()) {
            case "BRONZE" -> BigDecimal.valueOf(0.05);
            case "SILVER" -> BigDecimal.valueOf(0.10);
            case "GOLD" -> BigDecimal.valueOf(0.15);
            case "VIP" -> BigDecimal.valueOf(0.20);
            default -> BigDecimal.ZERO;
        };
    }
}
