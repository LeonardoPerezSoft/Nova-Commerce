package com.novacommerce.user_service.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Token Domain Model Tests")
class TokenTest {

    private Token token;

    @BeforeEach
    void setUp() {
        token = Token.builder()
                .value("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.dozjgNryP4J3jVmNHl0w5N_XgL0n3I9PlFUP0THsR8U")
                .username("testuser")
                .authorities("ROLE_ADMIN,ROLE_USER")
                .valid(true)
                .build();
    }

    @Test
    @DisplayName("Should create token with all required fields")
    void testTokenCreation() {
        assertNotNull(token);
        assertNotNull(token.getValue());
        assertEquals("testuser", token.getUsername());
        assertEquals("ROLE_ADMIN,ROLE_USER", token.getAuthorities());
        assertTrue(token.isValid());
    }

    @Test
    @DisplayName("Should use builder pattern correctly")
    void testBuilderPattern() {
        Token builtToken = Token.builder()
                .value("token123")
                .username("admin")
                .authorities("ROLE_ADMIN")
                .valid(true)
                .build();

        assertEquals("token123", builtToken.getValue());
        assertEquals("admin", builtToken.getUsername());
        assertEquals("ROLE_ADMIN", builtToken.getAuthorities());
        assertTrue(builtToken.isValid());
    }

    @Test
    @DisplayName("Should handle invalid token")
    void testInvalidToken() {
        Token invalidToken = Token.builder()
                .value("invalid_token")
                .username("testuser")
                .authorities("")
                .valid(false)
                .build();

        assertFalse(invalidToken.isValid());
    }

    @Test
    @DisplayName("Should handle empty authorities")
    void testEmptyAuthorities() {
        Token tokenWithNoAuthorities = Token.builder()
                .value("token123")
                .username("basicuser")
                .authorities("")
                .valid(true)
                .build();

        assertEquals("", tokenWithNoAuthorities.getAuthorities());
    }

    @Test
    @DisplayName("Should handle null username")
    void testNullUsername() {
        Token tokenWithNullUsername = Token.builder()
                .value("token123")
                .username(null)
                .authorities("ROLE_USER")
                .valid(true)
                .build();

        assertNull(tokenWithNullUsername.getUsername());
    }

    @Test
    @DisplayName("Should handle null value")
    void testNullValue() {
        Token tokenWithNullValue = Token.builder()
                .value(null)
                .username("testuser")
                .authorities("ROLE_USER")
                .valid(false)
                .build();

        assertNull(tokenWithNullValue.getValue());
    }

    @Test
    @DisplayName("Should handle multiple authorities")
    void testMultipleAuthorities() {
        Token multiAuthToken = Token.builder()
                .value("token123")
                .username("admin")
                .authorities("ROLE_ADMIN,ROLE_USER,ROLE_SALES")
                .valid(true)
                .build();

        assertEquals("ROLE_ADMIN,ROLE_USER,ROLE_SALES", multiAuthToken.getAuthorities());
    }

    @Test
    @DisplayName("Should create token with default constructor")
    void testDefaultConstructor() {
        Token emptyToken = new Token();
        assertNotNull(emptyToken);
        assertNull(emptyToken.getValue());
        assertNull(emptyToken.getUsername());
        assertNull(emptyToken.getAuthorities());
        assertFalse(emptyToken.isValid());
    }

    @Test
    @DisplayName("Should create token with all args constructor")
    void testAllArgsConstructor() {
        Token fullToken = new Token(
                "token_value",
                "username",
                "ROLE_ADMIN",
                true
        );

        assertEquals("token_value", fullToken.getValue());
        assertEquals("username", fullToken.getUsername());
        assertEquals("ROLE_ADMIN", fullToken.getAuthorities());
        assertTrue(fullToken.isValid());
    }

    @Test
    @DisplayName("Should handle long token values")
    void testLongTokenValue() {
        String longToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        
        Token tokenWithLongValue = Token.builder()
                .value(longToken)
                .username("testuser")
                .authorities("ROLE_USER")
                .valid(true)
                .build();

        assertEquals(longToken, tokenWithLongValue.getValue());
    }
}
