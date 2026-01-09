package com.novacommerce.auth_service.web.rest.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AuthServiceException Tests")
class AuthServiceExceptionTest {

    private static class TestException extends AuthServiceException {
        public TestException(String message) {
            super(message);
        }

        public TestException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @Test
    @DisplayName("Debe crear excepción con mensaje")
    void testExceptionWithMessage() {
        String message = "Test exception message";
        TestException exception = new TestException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Debe crear excepción con mensaje y causa")
    void testExceptionWithMessageAndCause() {
        String message = "Test exception message";
        Throwable cause = new RuntimeException("Root cause");
        TestException exception = new TestException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals("Root cause", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Debe heredar de RuntimeException")
    void testInheritance() {
        TestException exception = new TestException("Test");
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("Debe ser lanzable")
    void testThrowable() {
        assertThrows(TestException.class, () -> {
            throw new TestException("Test exception");
        });
    }

    @Test
    @DisplayName("Debe preservar stack trace con causa")
    void testStackTraceWithCause() {
        Exception cause = new IllegalArgumentException("Argumento inválido");
        TestException exception = new TestException("Error en servicio", cause);

        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
    }

    @Test
    @DisplayName("Debe permitir mensaje nulo")
    void testNullMessage() {
        TestException exception = new TestException(null);
        assertNull(exception.getMessage());
    }

    @Test
    @DisplayName("Debe permitir causa nula")
    void testNullCause() {
        TestException exception = new TestException("Mensaje", null);
        assertEquals("Mensaje", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Debe ser abstracta AuthServiceException")
    void testAbstractClass() {
        assertTrue(java.lang.reflect.Modifier.isAbstract(AuthServiceException.class.getModifiers()));
    }
}
