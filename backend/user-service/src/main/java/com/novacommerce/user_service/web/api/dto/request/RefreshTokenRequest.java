package com.novacommerce.user_service.web.api.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para la solicitud de refresco de token.
 */
public record RefreshTokenRequest(
    @NotBlank(message = "El refresh token es obligatorio")
    String refreshToken
) {}
