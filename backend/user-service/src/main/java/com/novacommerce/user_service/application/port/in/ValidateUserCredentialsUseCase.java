package com.novacommerce.user_service.application.port.in;

import com.novacommerce.user_service.web.api.dto.response.InternalUserValidationResponse;

/**
 * Puerto de entrada para validación interna de usuarios.
 * Este caso de uso es llamado por auth-service para validar credenciales.
 */
public interface ValidateUserCredentialsUseCase {

    /**
     * Valida las credenciales de un usuario.
     * 
     * @param userIdentifier username o email
     * @param password contraseña en texto plano
     * @return InternalUserValidationResponse con datos del usuario si las credenciales son válidas
     * @throws com.novacommerce.user_service.web.rest.exceptions.InvalidCredentialsException si las credenciales son inválidas
     */
    InternalUserValidationResponse validateCredentials(String userIdentifier, String password);
}
