package com.novacommerce.auth_service.config.security.jwt;

/**
 * Constantes de configuración para JWT.
 * Centraliza todos los valores utilizados en la generación y validación de tokens.
 */
public final class JwtConstants {

    private JwtConstants() {
        throw new AssertionError("No se puede instanciar esta clase de utilidad");
    }

    // Headers y prefijos
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final int TOKEN_START_INDEX = 7;

    // Claims
    public static final String AUTHORITIES_CLAIM = "authorities";

    // Mensajes de error
    public static final String INVALID_TOKEN_MESSAGE = "Token JWT inválido";
    public static final String EXPIRED_TOKEN_MESSAGE = "Token JWT expirado";
    public static final String UNSUPPORTED_TOKEN_MESSAGE = "Token JWT no soportado";
    public static final String EMPTY_CLAIMS_MESSAGE = "Token JWT con claims vacíos";
}
