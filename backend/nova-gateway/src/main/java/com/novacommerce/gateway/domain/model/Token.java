package com.novacommerce.gateway.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Modelo de dominio para representar un token JWT validado
 */
@Getter
@Builder
@AllArgsConstructor
@ToString
public class Token {

    private final String value;
    private final String username;
    private final List<String> authorities;
    private final LocalDateTime expirationTime;
    private final boolean valid;

    public boolean isExpired(LocalDateTime now) {
        return expirationTime.isBefore(now);
    }

    public boolean hasAuthority(String authority) {
        return authorities != null && authorities.contains(authority);
    }
}
