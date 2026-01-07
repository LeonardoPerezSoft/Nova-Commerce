package com.novacommerce.auth_service.web.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO de respuesta para login.
 * Contiene los tokens de acceso y refresco junto con la información de expiración y datos del usuario.
 */
public record LoginResponse(
    @JsonProperty("access_token")
    String accessToken,

    @JsonProperty("refresh_token")
    String refreshToken,

    @JsonProperty("token_type")
    String tokenType,

    @JsonProperty("expires_in")
    Long expiresIn,

    @JsonProperty("username")
    String username,

    @JsonProperty("roles")
    List<String> roles
) {
    /**
     * Factory method para crear LoginResponse con tipo de token Bearer.
     */
    public static LoginResponse bearer(String accessToken, String refreshToken, Long expiresIn) {
        return new LoginResponse(accessToken, refreshToken, "Bearer", expiresIn, null, null);
    }
}
