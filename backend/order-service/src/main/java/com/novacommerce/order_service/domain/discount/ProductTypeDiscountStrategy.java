package com.novacommerce.order_service.domain.discount;

import com.novacommerce.order_service.domain.model.DiscountContext;
import com.novacommerce.order_service.domain.model.DiscountResult;
import com.novacommerce.order_service.domain.model.Money;
import com.novacommerce.order_service.domain.model.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Estrategia de descuento basada en tipo de producto.
 * 
 * Reglas:
 * - ELECTRONICS: 8%
 * - CLOTHING: 12%
 * - FOOD: 3%
 */
@Component
public class ProductTypeDiscountStrategy implements DiscountStrategy {

    @Override
    public DiscountResult apply(DiscountContext context) {
        if (!isApplicable(context)) {
            return DiscountResult.noDiscount(getName());
        }

        Money totalDiscount = Money.zero();
        StringBuilder description = new StringBuilder("Product type discounts: ");

        for (OrderItem item : context.getItems()) {
            BigDecimal percentage = getProductTypePercentage(item.getProductType());
            if (percentage.compareTo(BigDecimal.ZERO) > 0) {
                Money itemSubTotal = item.calculateSubTotal();
                Money itemDiscount = itemSubTotal.multiply(percentage);
                totalDiscount = totalDiscount.add(itemDiscount);
                
                description.append(String.format("%s (%s%%), ", 
                        item.getProductType(), 
                        percentage.multiply(BigDecimal.valueOf(100))));
            }
        }

        if (totalDiscount.isZero()) {
            return DiscountResult.noDiscount(getName());
        }

        return DiscountResult.builder()
                .discountAmount(totalDiscount)
                .description(description.toString().replaceAll(", $", ""))
                .strategyName(getName())
                .build();
    }

    @Override
    public String getName() {
        return "ProductTypeDiscount";
    }

    @Override
    public boolean isApplicable(DiscountContext context) {
        return context.getItems() != null && !context.getItems().isEmpty();
    }

    private BigDecimal getProductTypePercentage(String productType) {
        if (productType == null) {
            return BigDecimal.ZERO;
        }
        
        return switch (productType.toUpperCase()) {
            case "ELECTRONICS" -> BigDecimal.valueOf(0.08);
            case "CLOTHING" -> BigDecimal.valueOf(0.12);
            case "FOOD" -> BigDecimal.valueOf(0.03);
            default -> BigDecimal.ZERO;
        };
    }
}
