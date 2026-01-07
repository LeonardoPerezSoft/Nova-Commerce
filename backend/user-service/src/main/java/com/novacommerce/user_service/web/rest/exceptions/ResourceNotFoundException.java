package com.novacommerce.user_service.web.rest.exceptions;

/**
 * Excepción lanzada cuando no se encuentra un recurso.
 */
public class ResourceNotFoundException extends AuthServiceException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s no encontrado con %s = %s", resourceName, fieldName, fieldValue));
    }
}
