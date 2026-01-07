package com.novacommerce.gateway.adapter.out.jwt;

import com.novacommerce.gateway.application.port.TokenValidatorPort;
import com.novacommerce.gateway.domain.exception.AuthenticationException;
import com.novacommerce.gateway.domain.model.Token;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * Adaptador de salida para validación de tokens JWT
 * Implementa la interfaz TokenValidatorPort usando la librería JJWT
 */
@Slf4j
@Component
public class JwtTokenValidatorAdapter implements TokenValidatorPort {

    private final SecretKey secretKey;

    public JwtTokenValidatorAdapter(@Value("${app.jwt.secret}") String secret) {
        this.secretKey = getSigningKey(secret);
    }

    /**
     * Decodifica la clave secreta de Base64 o UTF-8
     */
    private SecretKey getSigningKey(String jwtSecret) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(jwtSecret);
            log.debug("JWT Secret decoded as Base64. Bytes: {}, Bits: {}", decodedKey.length, decodedKey.length * 8);
            return Keys.hmacShaKeyFor(decodedKey);
        } catch (IllegalArgumentException ex) {
            log.warn("JWT Secret is not valid Base64, using as UTF-8");
            byte[] secretBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
            return Keys.hmacShaKeyFor(secretBytes);
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            log.debug("Token validated successfully");
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Extrae y parsea las claims del token
     */
    private Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new AuthenticationException("Failed to extract claims from token", "INVALID_TOKEN", e);
        }
    }

    @Override
    public String extractUsername(String token) {
        try {
            return extractClaims(token).getSubject();
        } catch (Exception e) {
            throw new AuthenticationException("Cannot extract username from token", "INVALID_TOKEN", e);
        }
    }

    @Override
    public String extractAuthorities(String token) {
        try {
            Claims claims = extractClaims(token);
            Object authoritiesObj = claims.get("authorities");

            if (authoritiesObj == null) {
                return "";
            }

            if (authoritiesObj instanceof String) {
                return (String) authoritiesObj;
            }

            if (authoritiesObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> authorities = (List<String>) authoritiesObj;
                if (authorities.isEmpty()) {
                    return "";
                }
                return String.join(",", authorities);
            }

            return "";
        } catch (Exception e) {
            throw new AuthenticationException("Cannot extract authorities from token", "INVALID_TOKEN", e);
        }
    }

    @Override
    public Token extractToken(String token) {
        try {
            Claims claims = extractClaims(token);

            String username = claims.getSubject();
            String authoritiesStr = extractAuthorities(token);
            List<String> authorities = authoritiesStr.isEmpty() ? 
                    List.of() : List.of(authoritiesStr.split(","));
            Date expiration = claims.getExpiration();
            LocalDateTime expirationTime = Instant.ofEpochMilli(expiration.getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            return Token.builder()
                    .value(token)
                    .username(username)
                    .authorities(authorities)
                    .expirationTime(expirationTime)
                    .valid(true)
                    .build();
        } catch (Exception e) {
            throw new AuthenticationException("Cannot extract token model", "INVALID_TOKEN", e);
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            throw new AuthenticationException("Cannot check token expiration", "INVALID_TOKEN", e);
        }
    }
}
