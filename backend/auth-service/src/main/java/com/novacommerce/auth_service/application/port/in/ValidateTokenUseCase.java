package com.novacommerce.auth_service.application.port.in;

import com.novacommerce.auth_service.web.api.dto.response.TokenValidationResponse;

/**
 * Puerto de entrada (use case) para validar tokens JWT
 */
public interface ValidateTokenUseCase {
    
    /**
     * Valida un token JWT
     * 
     * @param token el token a validar
     * @return TokenValidationResponse con información del token
     */
    TokenValidationResponse validateToken(String token);
}
