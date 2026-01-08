package com.novacommerce.order_service.domain.exception;

/**
 * Excepción para violaciones de reglas de negocio.
 */
public class BusinessRuleException extends OrderException {
    public BusinessRuleException(String message) {
        super(message);
    }

    public BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}
