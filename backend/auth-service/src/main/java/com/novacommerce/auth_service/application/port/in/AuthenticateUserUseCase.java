package com.novacommerce.auth_service.application.port.in;

import com.novacommerce.auth_service.web.api.dto.request.LoginRequest;
import com.novacommerce.auth_service.web.api.dto.response.LoginResponse;

/**
 * Puerto de entrada (use case) para autenticar usuarios
 * Define el contrato para el caso de uso de autenticación
 */
public interface AuthenticateUserUseCase {
    
    /**
     * Autentica un usuario con sus credenciales
     * 
     * @param loginRequest credenciales del usuario
     * @return LoginResponse con tokens JWT
     */
    LoginResponse authenticate(LoginRequest loginRequest);
}
