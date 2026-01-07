package com.novacommerce.user_service.domain.exception;

/**
 * Excepción de dominio lanzada cuando hay errores relacionados con seguridad.
 */
public class SecurityException extends RuntimeException {
    public SecurityException(String message) {
        super(message);
    }

    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
