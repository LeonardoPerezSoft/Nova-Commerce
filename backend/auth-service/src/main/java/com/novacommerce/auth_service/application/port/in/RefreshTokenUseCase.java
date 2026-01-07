package com.novacommerce.auth_service.application.port.in;

import com.novacommerce.auth_service.web.api.dto.request.RefreshTokenRequest;
import com.novacommerce.auth_service.web.api.dto.response.LoginResponse;

/**
 * Puerto de entrada (use case) para refrescar tokens
 */
public interface RefreshTokenUseCase {
    
    /**
     * Refresca un token de acceso usando un refresh token
     * 
     * @param refreshTokenRequest contiene el refresh token
     * @return LoginResponse con nuevo access token
     */
    LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
