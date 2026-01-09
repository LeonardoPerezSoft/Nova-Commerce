package com.novacommerce.auth_service.web.api.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoginResponse DTO Tests")
class LoginResponseTest {

    @Test
    @DisplayName("Should create valid LoginResponse")
    void testValidLoginResponse() {
        // Given & When
        LoginResponse response = new LoginResponse(
            "access-token-123",
            "refresh-token-456",
            "Bearer",
            3600L,
            "testuser",
            List.of("ROLE_ADMIN", "ROLE_USER")
        );

        // Then
        assertNotNull(response);
        assertEquals("access-token-123", response.accessToken());
        assertEquals("refresh-token-456", response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(3600L, response.expiresIn());
        assertEquals("testuser", response.username());
        assertEquals(2, response.roles().size());
        assertTrue(response.roles().contains("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("Should create LoginResponse using bearer factory method")
    void testBearerFactoryMethod() {
        // Given & When
        LoginResponse response = LoginResponse.bearer(
            "access-token-123",
            "refresh-token-456",
            3600L
        );

        // Then
        assertNotNull(response);
        assertEquals("access-token-123", response.accessToken());
        assertEquals("refresh-token-456", response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(3600L, response.expiresIn());
        assertNull(response.username());
        assertNull(response.roles());
    }

    @Test
    @DisplayName("Should handle null roles")
    void testNullRoles() {
        // Given & When
        LoginResponse response = new LoginResponse(
            "access-token-123",
            "refresh-token-456",
            "Bearer",
            3600L,
            "testuser",
            null
        );

        // Then
        assertNotNull(response);
        assertNull(response.roles());
    }

    @Test
    @DisplayName("Should handle empty roles")
    void testEmptyRoles() {
        // Given & When
        LoginResponse response = new LoginResponse(
            "access-token-123",
            "refresh-token-456",
            "Bearer",
            3600L,
            "testuser",
            List.of()
        );

        // Then
        assertNotNull(response);
        assertTrue(response.roles().isEmpty());
    }

    @Test
    @DisplayName("Should be immutable record")
    void testImmutability() {
        // Given
        LoginResponse response1 = new LoginResponse(
            "token1",
            "refresh1",
            "Bearer",
            3600L,
            "user1",
            List.of("ROLE_USER")
        );
        LoginResponse response2 = new LoginResponse(
            "token1",
            "refresh1",
            "Bearer",
            3600L,
            "user1",
            List.of("ROLE_USER")
        );

        // Then
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }
}
