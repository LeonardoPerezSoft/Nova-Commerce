package com.novacommerce.auth_service.web.rest.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InvalidCredentialsException Tests")
class InvalidCredentialsExceptionTest {

    @Test
    @DisplayName("Should create exception with custom message")
    void testCustomMessage() {
        // Given
        String message = "Usuario o contraseña incorrectos";

        // When
        InvalidCredentialsException exception = new InvalidCredentialsException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Should create exception with default message")
    void testDefaultMessage() {
        // When
        InvalidCredentialsException exception = new InvalidCredentialsException();

        // Then
        assertNotNull(exception);
        assertEquals("Credenciales inválidas", exception.getMessage());
    }

    @Test
    @DisplayName("Should be instance of AuthServiceException")
    void testInheritance() {
        // When
        InvalidCredentialsException exception = new InvalidCredentialsException();

        // Then
        assertInstanceOf(AuthServiceException.class, exception);
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("Should be throwable")
    void testThrowable() {
        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> {
            throw new InvalidCredentialsException("Test error");
        });
    }
}
