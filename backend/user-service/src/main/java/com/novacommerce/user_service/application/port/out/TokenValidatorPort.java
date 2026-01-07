package com.novacommerce.user_service.application.port.out;

import com.novacommerce.user_service.domain.model.Token;

/**
 * Puerto de salida para validación de tokens JWT.
 * Define las operaciones necesarias para validar y extraer información de tokens JWT.
 */
public interface TokenValidatorPort {

    /**
     * Valida un token JWT.
     * 
     * @param token el token JWT a validar
     * @return Token con información del token si es válido
     */
    Token validateToken(String token);

    /**
     * Verifica si un token es válido.
     * 
     * @param token el token JWT a verificar
     * @return true si el token es válido, false en caso contrario
     */
    boolean isValid(String token);

    /**
     * Extrae el username de un token JWT.
     * 
     * @param token el token JWT
     * @return el username
     */
    String getUsernameFromToken(String token);
}
