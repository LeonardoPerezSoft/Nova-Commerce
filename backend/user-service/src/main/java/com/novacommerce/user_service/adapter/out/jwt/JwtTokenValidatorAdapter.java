package com.novacommerce.user_service.adapter.out.jwt;

import com.novacommerce.user_service.application.port.out.TokenValidatorPort;
import com.novacommerce.user_service.domain.exception.SecurityException;
import com.novacommerce.user_service.domain.model.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Adaptador de salida para validación de tokens JWT.
 * Implementa TokenValidatorPort usando JJWT.
 */
@Component
@Slf4j
public class JwtTokenValidatorAdapter implements TokenValidatorPort {

    private final SecretKey secretKey;

    public JwtTokenValidatorAdapter(@Value("${app.jwt.secret}") String secret) {
        this.secretKey = getSigningKey(secret);
    }

    /**
     * Obtiene la clave de firma intentando decodificar Base64 primero.
     * Si no es Base64 válida, usa los bytes UTF-8 de la cadena.
     */
    private SecretKey getSigningKey(String jwtSecret) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(jwtSecret);
            log.debug("JWT Secret decodificado como Base64. Bytes: {}, Bits: {}", decodedKey.length, decodedKey.length * 8);
            return Keys.hmacShaKeyFor(decodedKey);
        } catch (IllegalArgumentException ex) {
            log.warn("JWT Secret no es Base64 válido, usando como UTF-8");
            byte[] secretBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
            return Keys.hmacShaKeyFor(secretBytes);
        }
    }

    @Override
    public Token validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

            String username = claims.getSubject();
            String authorities = claims.get("authorities", String.class);

            return Token.builder()
                .value(token)
                .username(username)
                .authorities(authorities)
                .valid(true)
                .build();

        } catch (Exception e) {
            log.error("Error validando token: {}", e.getMessage());
            return Token.builder()
                .value(token)
                .valid(false)
                .build();
        }
    }

    @Override
    public boolean isValid(String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.debug("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

            return claims.getSubject();
        } catch (Exception e) {
            log.error("Error extrayendo username del token: {}", e.getMessage());
            throw new SecurityException("Error extrayendo username del token", e);
        }
    }
}
