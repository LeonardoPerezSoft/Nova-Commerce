package com.novacommerce.order_service.application.port.in;

import com.novacommerce.order_service.domain.model.Order;

/**
 * Puerto de entrada (use case) para crear órdenes.
 */
public interface CreateOrderUseCase {
    
    /**
     * Crea una nueva orden validando reglas de negocio.
     * 
     * @param order orden a crear
     * @return orden creada con ID asignado
     */
    Order createOrder(Order order);
}
