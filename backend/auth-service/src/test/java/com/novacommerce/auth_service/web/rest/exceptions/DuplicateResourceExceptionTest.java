package com.novacommerce.auth_service.web.rest.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DuplicateResourceException Tests")
class DuplicateResourceExceptionTest {

    @Test
    @DisplayName("Should create exception with custom message")
    void testCustomMessage() {
        // Given
        String message = "El recurso ya existe";

        // When
        DuplicateResourceException exception = new DuplicateResourceException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Should create exception with resource details")
    void testResourceDetailsConstructor() {
        // Given
        String resourceName = "Usuario";
        String fieldName = "email";
        String fieldValue = "test@example.com";

        // When
        DuplicateResourceException exception = new DuplicateResourceException(
            resourceName, fieldName, fieldValue
        );

        // Then
        assertNotNull(exception);
        assertEquals("Usuario con email = test@example.com ya existe", exception.getMessage());
    }

    @Test
    @DisplayName("Should format message correctly with different resource types")
    void testFormattedMessageWithDifferentResources() {
        // Test with User
        DuplicateResourceException userException = new DuplicateResourceException(
            "User", "username", "admin"
        );
        assertEquals("User con username = admin ya existe", userException.getMessage());

        // Test with Product
        DuplicateResourceException productException = new DuplicateResourceException(
            "Product", "sku", "PROD-001"
        );
        assertEquals("Product con sku = PROD-001 ya existe", productException.getMessage());

        // Test with Order
        DuplicateResourceException orderException = new DuplicateResourceException(
            "Order", "id", 12345
        );
        assertEquals("Order con id = 12345 ya existe", orderException.getMessage());
    }

    @Test
    @DisplayName("Should be instance of AuthServiceException")
    void testInheritance() {
        // When
        DuplicateResourceException exception = new DuplicateResourceException("Test");

        // Then
        assertInstanceOf(AuthServiceException.class, exception);
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("Should be throwable")
    void testThrowable() {
        // When & Then
        assertThrows(DuplicateResourceException.class, () -> {
            throw new DuplicateResourceException("Test exception");
        });
    }

    @Test
    @DisplayName("Should handle null message")
    void testNullMessage() {
        // When
        DuplicateResourceException exception = new DuplicateResourceException(null);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    @Test
    @DisplayName("Should handle empty message")
    void testEmptyMessage() {
        // Given
        String message = "";

        // When
        DuplicateResourceException exception = new DuplicateResourceException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Should handle numeric field values")
    void testNumericFieldValue() {
        // When
        DuplicateResourceException exception = new DuplicateResourceException(
            "Customer", "id", 999L
        );

        // Then
        assertEquals("Customer con id = 999 ya existe", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle special characters in field values")
    void testSpecialCharactersInFieldValue() {
        // When
        DuplicateResourceException exception = new DuplicateResourceException(
            "User", "email", "user+test@example.com"
        );

        // Then
        assertTrue(exception.getMessage().contains("user+test@example.com"));
    }

    @Test
    @DisplayName("Should preserve exception stack trace")
    void testStackTrace() {
        // When
        DuplicateResourceException exception = new DuplicateResourceException("Test");

        // Then
        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
    }

    @Test
    @DisplayName("Should handle complex field values")
    void testComplexFieldValue() {
        // Given
        Object complexValue = new Object() {
            @Override
            public String toString() {
                return "ComplexObject{id=123}";
            }
        };

        // When
        DuplicateResourceException exception = new DuplicateResourceException(
            "Entity", "data", complexValue
        );

        // Then
        assertTrue(exception.getMessage().contains("ComplexObject{id=123}"));
    }

    @Test
    @DisplayName("Should work in catch-throw scenario")
    void testCatchThrowScenario() {
        // Given
        String expectedMessage = "User con email = duplicate@example.com ya existe";

        // When & Then
        Exception caughtException = assertThrows(DuplicateResourceException.class, () -> {
            throw new DuplicateResourceException("User", "email", "duplicate@example.com");
        });

        assertEquals(expectedMessage, caughtException.getMessage());
    }

    @Test
    @DisplayName("Should handle whitespace in resource name and field name")
    void testWhitespaceInParameters() {
        // When
        DuplicateResourceException exception = new DuplicateResourceException(
            "Resource Name", "field name", "value"
        );

        // Then
        assertEquals("Resource Name con field name = value ya existe", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle null field value")
    void testNullFieldValue() {
        // When
        DuplicateResourceException exception = new DuplicateResourceException(
            "User", "email", null
        );

        // Then
        assertEquals("User con email = null ya existe", exception.getMessage());
    }
}
