package com.novacommerce.user_service.web.rest.exceptions;

/**
 * Excepción lanzada cuando hay un error de autenticación.
 */
public class InvalidCredentialsException extends AuthServiceException {

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException() {
        super("Credenciales inválidas");
    }
}
