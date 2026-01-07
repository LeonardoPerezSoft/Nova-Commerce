package com.novacommerce.gateway.application.port;

import com.novacommerce.gateway.domain.model.Token;

/**
 * Puerto (interfaz) para validar tokens JWT
 * Define el contrato que deben implementar los adaptadores de validación de tokens
 */
public interface TokenValidatorPort {

    /**
     * Valida un token JWT
     *
     * @param token el token a validar
     * @return true si el token es válido, false en caso contrario
     */
    boolean validateToken(String token);

    /**
     * Extrae el nombre de usuario del token
     *
     * @param token el token JWT
     * @return el nombre de usuario
     * @throws com.novacommerce.gateway.domain.exception.AuthenticationException si el token es inválido
     */
    String extractUsername(String token);

    /**
     * Extrae las autoridades del token como una cadena separada por comas
     *
     * @param token el token JWT
     * @return las autoridades separadas por comas
     * @throws com.novacommerce.gateway.domain.exception.AuthenticationException si el token es inválido
     */
    String extractAuthorities(String token);

    /**
     * Extrae el modelo de Token con toda la información
     *
     * @param token el token JWT
     * @return el modelo Token con información validada
     * @throws com.novacommerce.gateway.domain.exception.AuthenticationException si el token es inválido
     */
    Token extractToken(String token);
}
