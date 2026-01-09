package com.novacommerce.auth_service.web.api.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TokenValidationResponse DTO Tests")
class TokenValidationResponseTest {

    @Test
    @DisplayName("Should create valid TokenValidationResponse")
    void testValidTokenValidationResponse() {
        // Given & When
        TokenValidationResponse response = new TokenValidationResponse(
            true,
            "testuser",
            "ROLE_ADMIN,USER_READ"
        );

        // Then
        assertNotNull(response);
        assertTrue(response.valid());
        assertEquals("testuser", response.username());
        assertEquals("ROLE_ADMIN,USER_READ", response.authorities());
    }

    @Test
    @DisplayName("Should create invalid TokenValidationResponse")
    void testInvalidTokenValidationResponse() {
        // Given & When
        TokenValidationResponse response = new TokenValidationResponse(
            false,
            null,
            null
        );

        // Then
        assertNotNull(response);
        assertFalse(response.valid());
        assertNull(response.username());
        assertNull(response.authorities());
    }

    @Test
    @DisplayName("Should handle empty authorities")
    void testEmptyAuthorities() {
        // Given & When
        TokenValidationResponse response = new TokenValidationResponse(
            true,
            "testuser",
            ""
        );

        // Then
        assertNotNull(response);
        assertTrue(response.valid());
        assertEquals("", response.authorities());
    }

    @Test
    @DisplayName("Should be immutable record")
    void testImmutability() {
        // Given
        TokenValidationResponse response1 = new TokenValidationResponse(
            true,
            "user1",
            "ROLE_USER"
        );
        TokenValidationResponse response2 = new TokenValidationResponse(
            true,
            "user1",
            "ROLE_USER"
        );

        // Then
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    @DisplayName("Should create valid response using factory method")
    void testValidFactoryMethod() {
        // Given & When
        TokenValidationResponse response = TokenValidationResponse.valid(
            "testuser",
            "ROLE_ADMIN,USER_READ"
        );

        // Then
        assertNotNull(response);
        assertTrue(response.valid());
        assertEquals("testuser", response.username());
        assertEquals("ROLE_ADMIN,USER_READ", response.authorities());
    }

    @Test
    @DisplayName("Should create invalid response using factory method")
    void testInvalidFactoryMethod() {
        // Given & When
        TokenValidationResponse response = TokenValidationResponse.invalid();

        // Then
        assertNotNull(response);
        assertFalse(response.valid());
        assertNull(response.username());
        assertNull(response.authorities());
    }

    @Test
    @DisplayName("Should handle null values in valid factory method")
    void testValidFactoryMethodWithNulls() {
        // Given & When
        TokenValidationResponse response = TokenValidationResponse.valid(null, null);

        // Then
        assertNotNull(response);
        assertTrue(response.valid());
        assertNull(response.username());
        assertNull(response.authorities());
    }
}
