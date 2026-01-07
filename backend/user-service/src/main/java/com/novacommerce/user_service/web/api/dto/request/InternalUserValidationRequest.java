package com.novacommerce.user_service.web.api.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de solicitud para validación de credenciales de usuario.
 * Usado por auth-service para validar login.
 */
public record InternalUserValidationRequest(
    @NotBlank(message = "El identificador de usuario es requerido")
    String userIdentifier,

    @NotBlank(message = "La contraseña es requerida")
    String password
) {
}
