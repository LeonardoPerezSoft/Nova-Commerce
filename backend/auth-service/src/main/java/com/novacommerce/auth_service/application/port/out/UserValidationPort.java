package com.novacommerce.auth_service.application.port.out;

import com.novacommerce.auth_service.client.dto.UserValidationRequest;
import com.novacommerce.auth_service.client.dto.UserValidationResponse;

/**
 * Puerto de salida para validar usuarios con el servicio externo
 * Define el contrato para comunicación con user-service
 */
public interface UserValidationPort {
    
    /**
     * Valida las credenciales de un usuario consultando a user-service
     * 
     * @param request contiene userIdentifier y password
     * @return UserValidationResponse con información del usuario
     */
    UserValidationResponse validateCredentials(UserValidationRequest request);
}
