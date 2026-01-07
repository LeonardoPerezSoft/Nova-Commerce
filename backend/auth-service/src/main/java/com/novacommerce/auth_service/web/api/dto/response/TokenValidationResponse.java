package com.novacommerce.auth_service.web.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para validación de token.
 * Confirma si el token es válido y proporciona información del usuario.
 */
public record TokenValidationResponse(
    @JsonProperty("valid")
    Boolean valid,

    @JsonProperty("username")
    String username,

    @JsonProperty("authorities")
    String authorities
) {
    /**
     * Factory method para un token válido.
     */
    public static TokenValidationResponse valid(String username, String authorities) {
        return new TokenValidationResponse(true, username, authorities);
    }

    /**
     * Factory method para un token inválido.
     */
    public static TokenValidationResponse invalid() {
        return new TokenValidationResponse(false, null, null);
    }
}
