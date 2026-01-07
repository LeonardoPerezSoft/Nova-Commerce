package com.novacommerce.auth_service.web.api.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para la solicitud de login.
 * Acepta usuario o email más contraseña.
 */
public record LoginRequest(
    @NotBlank(message = "El usuario o email es obligatorio")
    String userIdentifier,

    @NotBlank(message = "La contraseña es obligatoria")
    String password
) {}
