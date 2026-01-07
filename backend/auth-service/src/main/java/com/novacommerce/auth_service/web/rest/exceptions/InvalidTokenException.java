package com.novacommerce.auth_service.web.rest.exceptions;

/**
 * Excepción lanzada cuando hay un error con el token JWT.
 */
public class InvalidTokenException extends AuthServiceException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException() {
        super("Token inválido o expirado");
    }
}
