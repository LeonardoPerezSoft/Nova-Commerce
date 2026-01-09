package com.novacommerce.product_service.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CategoryException Tests")
class CategoryExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void testExceptionWithMessage() {
        String errorMessage = "Category not found";
        
        CategoryException exception = new CategoryException(errorMessage);
        
        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Should create exception with message and cause")
    void testExceptionWithMessageAndCause() {
        String errorMessage = "Failed to process category";
        Throwable cause = new IllegalArgumentException("Invalid category ID");
        
        CategoryException exception = new CategoryException(errorMessage, cause);
        
        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals("Invalid category ID", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void testIsRuntimeException() {
        CategoryException exception = new CategoryException("Test");
        
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Should allow null cause")
    void testNullCause() {
        CategoryException exception = new CategoryException("Test message", null);
        
        assertNotNull(exception);
        assertEquals("Test message", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Should preserve stack trace")
    void testStackTrace() {
        CategoryException exception = new CategoryException("Test exception");
        
        StackTraceElement[] stackTrace = exception.getStackTrace();
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);
    }

    @Test
    @DisplayName("Should handle nested exceptions")
    void testNestedExceptions() {
        Throwable rootCause = new IllegalStateException("Root cause");
        Throwable intermediateCause = new CategoryException("Intermediate", rootCause);
        CategoryException exception = new CategoryException("Top level", intermediateCause);
        
        assertEquals("Top level", exception.getMessage());
        assertEquals(intermediateCause, exception.getCause());
        assertEquals(rootCause, exception.getCause().getCause());
    }

    @Test
    @DisplayName("Should handle empty message")
    void testEmptyMessage() {
        CategoryException exception = new CategoryException("");
        
        assertNotNull(exception);
        assertEquals("", exception.getMessage());
    }

    @Test
    @DisplayName("Should be throwable")
    void testIsThrowable() {
        CategoryException exception = new CategoryException("Test");
        
        assertThrows(CategoryException.class, () -> {
            throw exception;
        });
    }

    @Test
    @DisplayName("Should carry cause exception type information")
    void testCauseType() {
        NullPointerException cause = new NullPointerException("Null category");
        CategoryException exception = new CategoryException("Category error", cause);
        
        assertTrue(exception.getCause() instanceof NullPointerException);
    }

    @Test
    @DisplayName("Should handle exception in catch block")
    void testCatchBlock() {
        try {
            throw new CategoryException("Test exception");
        } catch (CategoryException e) {
            assertEquals("Test exception", e.getMessage());
            assertTrue(e instanceof RuntimeException);
        }
    }
}
