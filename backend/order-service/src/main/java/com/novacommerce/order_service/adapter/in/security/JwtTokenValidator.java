package com.novacommerce.order_service.adapter.in.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.List;

/**
 * Validador de tokens JWT.
 */
@Component
public class JwtTokenValidator {

    private final SecretKey signingKey;

    public JwtTokenValidator(@Value("${app.jwt.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public boolean isValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getAuthorities(String token) {
        var claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        Object authoritiesObj = claims.get("authorities");
        
        if (authoritiesObj == null) {
            return List.of();
        }
        
        // Si es una List, retornarla directamente
        if (authoritiesObj instanceof List<?>) {
            return (List<String>) authoritiesObj;
        }
        
        // Si es una String, parsearla
        if (authoritiesObj instanceof String) {
            String authStr = ((String) authoritiesObj).trim();
            if (authStr.isEmpty()) {
                return List.of();
            }
            
            // Si comienza con "[", es un JSON array, parsearlo
            if (authStr.startsWith("[")) {
                try {
                    // Remover corchetes y comillas para obtener la lista
                    authStr = authStr.replace("[", "")
                            .replace("]", "")
                            .replace("\"", "");
                    
                    return List.of(authStr.split(","))
                            .stream()
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(java.util.stream.Collectors.toList());
                } catch (Exception e) {
                    return List.of();
                }
            }
            
            // Si no comienza con "[", es una cadena separada por comas
            return List.of(authStr.split(","))
                    .stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(java.util.stream.Collectors.toList());
        }
        
        return List.of();
    }
}
