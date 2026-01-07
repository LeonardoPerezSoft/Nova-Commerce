package com.novacommerce.auth_service.client.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitud de validación de credenciales al user-service.
 * Se envía durante el proceso de login.
 */
public record UserValidationRequest(
    @NotBlank(message = "El identificador de usuario es obligatorio")
    String userIdentifier,
    
    @NotBlank(message = "La contraseña es obligatoria")
    String password
) {
}
