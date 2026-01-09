package com.novacommerce.product_service.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductException Tests")
class ProductExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void testExceptionWithMessage() {
        String errorMessage = "Product not found";
        
        ProductException exception = new ProductException(errorMessage);
        
        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Should create exception with message and cause")
    void testExceptionWithMessageAndCause() {
        String errorMessage = "Failed to process product";
        Throwable cause = new IllegalArgumentException("Invalid product ID");
        
        ProductException exception = new ProductException(errorMessage, cause);
        
        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals("Invalid product ID", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void testIsRuntimeException() {
        ProductException exception = new ProductException("Test");
        
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Should allow null cause")
    void testNullCause() {
        ProductException exception = new ProductException("Test message", null);
        
        assertNotNull(exception);
        assertEquals("Test message", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Should preserve stack trace")
    void testStackTrace() {
        ProductException exception = new ProductException("Test exception");
        
        StackTraceElement[] stackTrace = exception.getStackTrace();
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);
    }

    @Test
    @DisplayName("Should handle nested exceptions")
    void testNestedExceptions() {
        Throwable rootCause = new IllegalStateException("Root cause");
        Throwable intermediateCause = new ProductException("Intermediate", rootCause);
        ProductException exception = new ProductException("Top level", intermediateCause);
        
        assertEquals("Top level", exception.getMessage());
        assertEquals(intermediateCause, exception.getCause());
        assertEquals(rootCause, exception.getCause().getCause());
    }

    @Test
    @DisplayName("Should handle empty message")
    void testEmptyMessage() {
        ProductException exception = new ProductException("");
        
        assertNotNull(exception);
        assertEquals("", exception.getMessage());
    }

    @Test
    @DisplayName("Should be throwable")
    void testIsThrowable() {
        ProductException exception = new ProductException("Test");
        
        assertThrows(ProductException.class, () -> {
            throw exception;
        });
    }

    @Test
    @DisplayName("Should carry cause exception type information")
    void testCauseType() {
        NullPointerException cause = new NullPointerException("Null product");
        ProductException exception = new ProductException("Product error", cause);
        
        assertTrue(exception.getCause() instanceof NullPointerException);
    }

    @Test
    @DisplayName("Should handle exception in catch block")
    void testCatchBlock() {
        try {
            throw new ProductException("Test exception");
        } catch (ProductException e) {
            assertEquals("Test exception", e.getMessage());
            assertTrue(e instanceof RuntimeException);
        }
    }
}
