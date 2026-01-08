package com.novacommerce.order_service.application.port.out;

/**
 * Puerto de salida para validación de clientes.
 */
public interface CustomerValidationPort {
    
    /**
     * Valida que el cliente existe y está activo.
     * 
     * @param customerId ID del cliente
     * @return true si el cliente es válido
     */
    boolean isCustomerValid(Long customerId);
    
    /**
     * Obtiene el estado del cliente.
     */
    String getCustomerStatus(Long customerId);
    
    /**
     * Obtiene el nivel de fidelidad del cliente.
     */
    String getCustomerLoyaltyLevel(Long customerId);
}
