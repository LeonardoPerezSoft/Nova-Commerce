package com.novacommerce.order_service.domain.discount;

import com.novacommerce.order_service.domain.model.DiscountContext;
import com.novacommerce.order_service.domain.model.DiscountResult;

/**
 * Interfaz Strategy para descuentos.
 * Cada implementación define una estrategia diferente de descuento.
 */
public interface DiscountStrategy {
    
    /**
     * Aplica la estrategia de descuento al contexto dado.
     * 
     * @param context contexto con información de cliente, orden e items
     * @return resultado del descuento aplicado
     */
    DiscountResult apply(DiscountContext context);
    
    /**
     * Nombre de la estrategia para logging y debugging.
     */
    String getName();
    
    /**
     * Verifica si esta estrategia es aplicable al contexto dado.
     */
    boolean isApplicable(DiscountContext context);
}
