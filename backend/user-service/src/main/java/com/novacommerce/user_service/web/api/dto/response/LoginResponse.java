package com.novacommerce.user_service.web.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para login.
 * Contiene los tokens de acceso y refresco junto con la información de expiración.
 */
public record LoginResponse(
    @JsonProperty("access_token")
    String accessToken,

    @JsonProperty("refresh_token")
    String refreshToken,

    @JsonProperty("token_type")
    String tokenType,

    @JsonProperty("expires_in")
    Long expiresIn
) {
    /**
     * Factory method para crear LoginResponse con tipo de token Bearer.
     */
    public static LoginResponse bearer(String accessToken, String refreshToken, Long expiresIn) {
        return new LoginResponse(accessToken, refreshToken, "Bearer", expiresIn);
    }
}
