package com.novacommerce.order_service.domain.discount;

import com.novacommerce.order_service.domain.model.DiscountContext;
import com.novacommerce.order_service.domain.model.DiscountResult;
import com.novacommerce.order_service.domain.model.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Estrategia de descuento basada en temporada.
 * 
 * Reglas:
 * - WINTER: 15%
 * - SUMMER: 10%
 * - SPRING/FALL: 5%
 */
@Component
public class SeasonDiscountStrategy implements DiscountStrategy {

    @Override
    public DiscountResult apply(DiscountContext context) {
        if (!isApplicable(context)) {
            return DiscountResult.noDiscount(getName());
        }

        BigDecimal percentage = getSeasonPercentage(context.getCurrentSeason());
        if (percentage.compareTo(BigDecimal.ZERO) == 0) {
            return DiscountResult.noDiscount(getName());
        }

        Money discountAmount = context.getOrderTotal().multiply(percentage);

        return DiscountResult.builder()
                .discountAmount(discountAmount)
                .description(String.format("Season discount: %s (%s%%)", 
                        context.getCurrentSeason(), 
                        percentage.multiply(BigDecimal.valueOf(100))))
                .strategyName(getName())
                .build();
    }

    @Override
    public String getName() {
        return "SeasonDiscount";
    }

    @Override
    public boolean isApplicable(DiscountContext context) {
        return context.getCurrentSeason() != null && !context.getCurrentSeason().isBlank();
    }

    private BigDecimal getSeasonPercentage(String season) {
        return switch (season.toUpperCase()) {
            case "WINTER" -> BigDecimal.valueOf(0.15);
            case "SUMMER" -> BigDecimal.valueOf(0.10);
            case "SPRING", "FALL" -> BigDecimal.valueOf(0.05);
            default -> BigDecimal.ZERO;
        };
    }
}
