package com.novacommerce.gateway.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TokenTest {

    @Test
    void testTokenBuilderCreatesToken() {
        LocalDateTime expirationTime = LocalDateTime.now().plusHours(1);
        
        Token token = Token.builder()
            .value("test-token-value")
            .username("testuser")
            .authorities(List.of("ROLE_USER", "ROLE_ADMIN"))
            .expirationTime(expirationTime)
            .valid(true)
            .build();

        assertNotNull(token, "Token should be created");
        assertEquals("test-token-value", token.getValue(), "Value should match");
        assertEquals("testuser", token.getUsername(), "Username should match");
        assertEquals(2, token.getAuthorities().size(), "Should have 2 authorities");
        assertTrue(token.isValid(), "Should be valid");
    }

    @Test
    void testTokenIsExpiredWithFutureDate() {
        LocalDateTime futureDate = LocalDateTime.now().plusHours(1);
        Token token = Token.builder()
            .value("token")
            .username("user")
            .authorities(List.of())
            .expirationTime(futureDate)
            .valid(true)
            .build();

        assertFalse(token.isExpired(LocalDateTime.now()), "Token should not be expired");
    }

    @Test
    void testTokenIsExpiredWithPastDate() {
        LocalDateTime pastDate = LocalDateTime.now().minusHours(1);
        Token token = Token.builder()
            .value("token")
            .username("user")
            .authorities(List.of())
            .expirationTime(pastDate)
            .valid(true)
            .build();

        assertTrue(token.isExpired(LocalDateTime.now()), "Token should be expired");
    }

    @Test
    void testTokenHasAuthorityWithValidAuthority() {
        Token token = Token.builder()
            .value("token")
            .username("user")
            .authorities(List.of("ROLE_USER", "ROLE_ADMIN"))
            .expirationTime(LocalDateTime.now().plusHours(1))
            .valid(true)
            .build();

        assertTrue(token.hasAuthority("ROLE_ADMIN"), "Should have ROLE_ADMIN authority");
        assertTrue(token.hasAuthority("ROLE_USER"), "Should have ROLE_USER authority");
    }

    @Test
    void testTokenHasAuthorityWithInvalidAuthority() {
        Token token = Token.builder()
            .value("token")
            .username("user")
            .authorities(List.of("ROLE_USER"))
            .expirationTime(LocalDateTime.now().plusHours(1))
            .valid(true)
            .build();

        assertFalse(token.hasAuthority("ROLE_ADMIN"), "Should not have ROLE_ADMIN authority");
    }

    @Test
    void testTokenHasAuthorityWithEmptyAuthorities() {
        Token token = Token.builder()
            .value("token")
            .username("user")
            .authorities(List.of())
            .expirationTime(LocalDateTime.now().plusHours(1))
            .valid(true)
            .build();

        assertFalse(token.hasAuthority("ROLE_USER"), "Should not have any authorities");
    }

    @Test
    void testTokenHasAuthorityWithNullAuthorities() {
        Token token = Token.builder()
            .value("token")
            .username("user")
            .authorities(null)
            .expirationTime(LocalDateTime.now().plusHours(1))
            .valid(true)
            .build();

        assertFalse(token.hasAuthority("ROLE_USER"), "Should return false for null authorities");
    }

    @Test
    void testTokenInvalidFlag() {
        Token validToken = Token.builder()
            .value("token")
            .username("user")
            .authorities(List.of("ROLE_USER"))
            .expirationTime(LocalDateTime.now().plusHours(1))
            .valid(true)
            .build();

        Token invalidToken = Token.builder()
            .value("token")
            .username("user")
            .authorities(List.of("ROLE_USER"))
            .expirationTime(LocalDateTime.now().plusHours(1))
            .valid(false)
            .build();

        assertTrue(validToken.isValid(), "Valid token should return true");
        assertFalse(invalidToken.isValid(), "Invalid token should return false");
    }

    @Test
    void testTokenToString() {
        Token token = Token.builder()
            .value("token")
            .username("testuser")
            .authorities(List.of("ROLE_USER"))
            .expirationTime(LocalDateTime.now().plusHours(1))
            .valid(true)
            .build();

        String tokenString = token.toString();
        assertNotNull(tokenString, "toString should not be null");
        assertTrue(tokenString.contains("testuser"), "toString should contain username");
    }

    @Test
    void testTokenWithMultipleAuthorities() {
        Token token = Token.builder()
            .value("token")
            .username("admin")
            .authorities(List.of("ROLE_ADMIN", "ROLE_USER", "ROLE_SUPPORT", "ROLE_MANAGER"))
            .expirationTime(LocalDateTime.now().plusHours(1))
            .valid(true)
            .build();

        assertEquals(4, token.getAuthorities().size(), "Should have 4 authorities");
        assertTrue(token.hasAuthority("ROLE_ADMIN"), "Should have ROLE_ADMIN");
        assertTrue(token.hasAuthority("ROLE_USER"), "Should have ROLE_USER");
        assertTrue(token.hasAuthority("ROLE_SUPPORT"), "Should have ROLE_SUPPORT");
        assertTrue(token.hasAuthority("ROLE_MANAGER"), "Should have ROLE_MANAGER");
        assertFalse(token.hasAuthority("ROLE_GUEST"), "Should not have ROLE_GUEST");
    }

    @Test
    void testTokenExpirationTimeComparison() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureTime = now.plusSeconds(30);
        LocalDateTime pastTime = now.minusSeconds(30);

        Token token = Token.builder()
            .value("token")
            .username("user")
            .authorities(List.of("ROLE_USER"))
            .expirationTime(futureTime)
            .valid(true)
            .build();

        assertFalse(token.isExpired(pastTime), "Token should not be expired when now is before expiration");
        assertFalse(token.isExpired(now), "Token should not be expired at current time");
    }

    @Test
    void testTokenAllFieldsNonNull() {
        LocalDateTime expirationTime = LocalDateTime.now().plusHours(1);
        Token token = Token.builder()
            .value("value")
            .username("username")
            .authorities(List.of("ROLE_USER"))
            .expirationTime(expirationTime)
            .valid(true)
            .build();

        assertNotNull(token.getValue(), "Value should not be null");
        assertNotNull(token.getUsername(), "Username should not be null");
        assertNotNull(token.getAuthorities(), "Authorities should not be null");
        assertNotNull(token.getExpirationTime(), "ExpirationTime should not be null");
    }
}
