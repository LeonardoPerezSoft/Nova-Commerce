package com.novacommerce.auth_service.web.rest.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InvalidTokenException Tests")
class InvalidTokenExceptionTest {

    @Test
    @DisplayName("Should create exception with custom message")
    void testCustomMessage() {
        // Given
        String message = "El token ha expirado";

        // When
        InvalidTokenException exception = new InvalidTokenException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Should create exception with default message")
    void testDefaultMessage() {
        // When
        InvalidTokenException exception = new InvalidTokenException();

        // Then
        assertNotNull(exception);
        assertEquals("Token inválido o expirado", exception.getMessage());
    }

    @Test
    @DisplayName("Should be instance of AuthServiceException")
    void testInheritance() {
        // When
        InvalidTokenException exception = new InvalidTokenException();

        // Then
        assertInstanceOf(AuthServiceException.class, exception);
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("Should be throwable")
    void testThrowable() {
        // When & Then
        assertThrows(InvalidTokenException.class, () -> {
            throw new InvalidTokenException("Test token error");
        });
    }
}
