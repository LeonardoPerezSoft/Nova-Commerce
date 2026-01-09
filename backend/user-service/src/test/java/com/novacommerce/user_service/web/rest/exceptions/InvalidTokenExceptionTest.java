package com.novacommerce.user_service.web.rest.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InvalidTokenException Tests")
class InvalidTokenExceptionTest {

    @Test
    @DisplayName("Should create exception with custom message")
    void testCreateWithMessage() {
        String message = "Token expirado";
        
        InvalidTokenException exception = new InvalidTokenException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Should create exception with default message")
    void testCreateWithDefaultMessage() {
        InvalidTokenException exception = new InvalidTokenException();

        assertNotNull(exception);
        assertEquals("Token inválido o expirado", exception.getMessage());
    }

    @Test
    @DisplayName("Should extend AuthServiceException")
    void testInheritsFromAuthServiceException() {
        InvalidTokenException exception = new InvalidTokenException();
        
        assertTrue(exception instanceof AuthServiceException);
    }

    @Test
    @DisplayName("Should be RuntimeException")
    void testIsRuntimeException() {
        InvalidTokenException exception = new InvalidTokenException();
        
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Should throw and catch exception")
    void testThrowAndCatch() {
        String expectedMessage = "Token JWT inválido";
        
        assertThrows(InvalidTokenException.class, () -> {
            throw new InvalidTokenException(expectedMessage);
        });
    }

    @Test
    @DisplayName("Should preserve message when thrown")
    void testMessagePreservation() {
        String message = "Firma de token no válida";
        
        InvalidTokenException exception = assertThrows(
            InvalidTokenException.class,
            () -> { throw new InvalidTokenException(message); }
        );
        
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Should be distinguishable from other exceptions")
    void testExceptionDistinction() {
        InvalidTokenException tokenEx = new InvalidTokenException("Token inválido");
        InvalidCredentialsException credentialEx = new InvalidCredentialsException("Credenciales inválidas");
        
        assertNotEquals(
            tokenEx.getClass(),
            credentialEx.getClass()
        );
    }

    @Test
    @DisplayName("Should work with different messages")
    void testDifferentMessages() {
        InvalidTokenException ex1 = new InvalidTokenException("Mensaje 1");
        InvalidTokenException ex2 = new InvalidTokenException("Mensaje 2");
        
        assertNotEquals(ex1.getMessage(), ex2.getMessage());
    }

    @Test
    @DisplayName("Should work with empty message")
    void testEmptyMessage() {
        InvalidTokenException exception = new InvalidTokenException("");
        
        assertNotNull(exception);
        assertEquals("", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle null-like scenarios with default message")
    void testDefaultMessageFallback() {
        InvalidTokenException exception = new InvalidTokenException();
        
        assertNotNull(exception.getMessage());
        assertFalse(exception.getMessage().isEmpty());
    }

    @Test
    @DisplayName("Should be throwable and catchable")
    void testThrowableAndCatchable() {
        assertThrows(InvalidTokenException.class, () -> {
            throw new InvalidTokenException("Test");
        });
    }
}
