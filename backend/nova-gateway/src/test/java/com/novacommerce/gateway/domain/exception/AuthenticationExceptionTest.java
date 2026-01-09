package com.novacommerce.gateway.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationExceptionTest {

    @Test
    void testConstructorWithMessageOnly() {
        AuthenticationException exception = new AuthenticationException("Test message");
        
        assertEquals("Test message", exception.getMessage(), "Message should match");
        assertEquals("AUTH_ERROR", exception.getErrorCode(), "Error code should be default");
    }

    @Test
    void testConstructorWithMessageAndErrorCode() {
        AuthenticationException exception = new AuthenticationException("Test message", "INVALID_TOKEN");
        
        assertEquals("Test message", exception.getMessage(), "Message should match");
        assertEquals("INVALID_TOKEN", exception.getErrorCode(), "Error code should match");
    }

    @Test
    void testConstructorWithMessageAndCause() {
        Exception cause = new Exception("Cause message");
        AuthenticationException exception = new AuthenticationException("Test message", cause);
        
        assertEquals("Test message", exception.getMessage(), "Message should match");
        assertEquals("AUTH_ERROR", exception.getErrorCode(), "Error code should be default");
        assertEquals(cause, exception.getCause(), "Cause should match");
    }

    @Test
    void testConstructorWithMessageErrorCodeAndCause() {
        Exception cause = new Exception("Cause message");
        AuthenticationException exception = new AuthenticationException("Test message", "TOKEN_EXPIRED", cause);
        
        assertEquals("Test message", exception.getMessage(), "Message should match");
        assertEquals("TOKEN_EXPIRED", exception.getErrorCode(), "Error code should match");
        assertEquals(cause, exception.getCause(), "Cause should match");
    }

    @Test
    void testExceptionIsRuntimeException() {
        AuthenticationException exception = new AuthenticationException("Test");
        
        assertTrue(exception instanceof RuntimeException, "Should be a RuntimeException");
    }

    @Test
    void testExceptionCanBeThrownAndCaught() {
        assertThrows(AuthenticationException.class, () -> {
            throw new AuthenticationException("Test message", "ERROR_CODE");
        });
    }

    @Test
    void testMultipleErrorCodes() {
        String[] errorCodes = {"INVALID_TOKEN", "TOKEN_EXPIRED", "AUTH_ERROR", "MISSING_TOKEN"};
        
        for (String errorCode : errorCodes) {
            AuthenticationException exception = new AuthenticationException("Message", errorCode);
            assertEquals(errorCode, exception.getErrorCode(), "Error code should match: " + errorCode);
        }
    }

    @Test
    void testErrorCodeNotNull() {
        AuthenticationException exception = new AuthenticationException("Message");
        assertNotNull(exception.getErrorCode(), "Error code should not be null");
    }

    @Test
    void testMessageNotNull() {
        AuthenticationException exception = new AuthenticationException("Test message");
        assertNotNull(exception.getMessage(), "Message should not be null");
    }

    @Test
    void testCauseNotNullWhenProvided() {
        Exception cause = new RuntimeException("Root cause");
        AuthenticationException exception = new AuthenticationException("Message", cause);
        
        assertNotNull(exception.getCause(), "Cause should not be null");
        assertEquals(cause, exception.getCause(), "Cause should match");
    }

    @Test
    void testDefaultErrorCodeValue() {
        AuthenticationException exception1 = new AuthenticationException("Message");
        AuthenticationException exception2 = new AuthenticationException("Message", new Exception());
        
        assertEquals("AUTH_ERROR", exception1.getErrorCode(), "Default error code should be AUTH_ERROR");
        assertEquals("AUTH_ERROR", exception2.getErrorCode(), "Default error code should be AUTH_ERROR");
    }

    @Test
    void testStackTraceContainsErrorCode() {
        AuthenticationException exception = new AuthenticationException("Message", "CUSTOM_CODE");
        String stackTrace = exception.toString();
        
        assertNotNull(stackTrace, "Stack trace should not be null");
        assertTrue(stackTrace.contains("AuthenticationException"), "Stack trace should contain exception name");
    }
}
