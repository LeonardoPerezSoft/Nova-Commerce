package com.novacommerce.user_service.web.rest.exceptions;

/**
 * Excepción base para errores de negocio del auth-service.
 */
public abstract class AuthServiceException extends RuntimeException {

    public AuthServiceException(String message) {
        super(message);
    }

    public AuthServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
