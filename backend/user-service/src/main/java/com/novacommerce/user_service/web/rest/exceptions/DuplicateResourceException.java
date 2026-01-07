package com.novacommerce.user_service.web.rest.exceptions;

/**
 * Excepción lanzada cuando se intenta crear un recurso duplicado.
 */
public class DuplicateResourceException extends AuthServiceException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s con %s = %s ya existe", resourceName, fieldName, fieldValue));
    }
}
