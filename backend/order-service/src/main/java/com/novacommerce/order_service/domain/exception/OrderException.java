package com.novacommerce.order_service.domain.exception;

/**
 * Excepción base para errores relacionados con órdenes.
 */
public class OrderException extends RuntimeException {
    public OrderException(String message) {
        super(message);
    }

    public OrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
