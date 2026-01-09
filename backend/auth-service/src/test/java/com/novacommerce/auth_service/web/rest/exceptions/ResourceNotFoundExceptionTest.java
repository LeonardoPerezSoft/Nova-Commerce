package com.novacommerce.auth_service.web.rest.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ResourceNotFoundException Tests")
class ResourceNotFoundExceptionTest {

    @Test
    @DisplayName("Should create exception with custom message")
    void testCustomMessage() {
        // Given
        String message = "El recurso no fue encontrado";

        // When
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Should create exception with resource details")
    void testResourceDetailsConstructor() {
        // Given
        String resourceName = "Usuario";
        String fieldName = "id";
        Long fieldValue = 123L;

        // When
        ResourceNotFoundException exception = new ResourceNotFoundException(
            resourceName, fieldName, fieldValue
        );

        // Then
        assertNotNull(exception);
        assertEquals("Usuario no encontrado con id = 123", exception.getMessage());
    }

    @Test
    @DisplayName("Should format message correctly with different resource types")
    void testFormattedMessageWithDifferentResources() {
        // Test with User
        ResourceNotFoundException userException = new ResourceNotFoundException(
            "User", "username", "john.doe"
        );
        assertEquals("User no encontrado con username = john.doe", userException.getMessage());

        // Test with Product
        ResourceNotFoundException productException = new ResourceNotFoundException(
            "Product", "sku", "PROD-123"
        );
        assertEquals("Product no encontrado con sku = PROD-123", productException.getMessage());

        // Test with Order
        ResourceNotFoundException orderException = new ResourceNotFoundException(
            "Order", "id", 999
        );
        assertEquals("Order no encontrado con id = 999", orderException.getMessage());
    }

    @Test
    @DisplayName("Should be instance of AuthServiceException")
    void testInheritance() {
        // When
        ResourceNotFoundException exception = new ResourceNotFoundException("Test");

        // Then
        assertInstanceOf(AuthServiceException.class, exception);
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("Should be throwable")
    void testThrowable() {
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            throw new ResourceNotFoundException("Test exception");
        });
    }

    @Test
    @DisplayName("Should handle null message")
    void testNullMessage() {
        // When
        ResourceNotFoundException exception = new ResourceNotFoundException(null);

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
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Should handle numeric field values")
    void testNumericFieldValue() {
        // When
        ResourceNotFoundException exception = new ResourceNotFoundException(
            "Customer", "customerId", 12345L
        );

        // Then
        assertEquals("Customer no encontrado con customerId = 12345", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle UUID field values")
    void testUuidFieldValue() {
        // Given
        String uuid = "550e8400-e29b-41d4-a716-446655440000";

        // When
        ResourceNotFoundException exception = new ResourceNotFoundException(
            "Order", "uuid", uuid
        );

        // Then
        assertTrue(exception.getMessage().contains(uuid));
    }

    @Test
    @DisplayName("Should handle special characters in field values")
    void testSpecialCharactersInFieldValue() {
        // When
        ResourceNotFoundException exception = new ResourceNotFoundException(
            "User", "email", "user+test@example.com"
        );

        // Then
        assertTrue(exception.getMessage().contains("user+test@example.com"));
    }

    @Test
    @DisplayName("Should preserve exception stack trace")
    void testStackTrace() {
        // When
        ResourceNotFoundException exception = new ResourceNotFoundException("Test");

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
                return "ComplexObject{id=456}";
            }
        };

        // When
        ResourceNotFoundException exception = new ResourceNotFoundException(
            "Entity", "data", complexValue
        );

        // Then
        assertTrue(exception.getMessage().contains("ComplexObject{id=456}"));
    }

    @Test
    @DisplayName("Should work in catch-throw scenario")
    void testCatchThrowScenario() {
        // Given
        String expectedMessage = "User no encontrado con id = 999";

        // When & Then
        Exception caughtException = assertThrows(ResourceNotFoundException.class, () -> {
            throw new ResourceNotFoundException("User", "id", 999);
        });

        assertEquals(expectedMessage, caughtException.getMessage());
    }

    @Test
    @DisplayName("Should handle whitespace in resource name and field name")
    void testWhitespaceInParameters() {
        // When
        ResourceNotFoundException exception = new ResourceNotFoundException(
            "Resource Name", "field name", "value"
        );

        // Then
        assertEquals("Resource Name no encontrado con field name = value", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle null field value")
    void testNullFieldValue() {
        // When
        ResourceNotFoundException exception = new ResourceNotFoundException(
            "User", "token", null
        );

        // Then
        assertEquals("User no encontrado con token = null", exception.getMessage());
    }

    @Test
    @DisplayName("Should differentiate between ResourceNotFoundException and DuplicateResourceException")
    void testDifferenceFromDuplicateException() {
        // Given
        ResourceNotFoundException notFound = new ResourceNotFoundException("User", "id", 1);
        DuplicateResourceException duplicate = new DuplicateResourceException("User", "id", 1);

        // Then
        assertNotEquals(notFound.getMessage(), duplicate.getMessage());
        assertTrue(notFound.getMessage().contains("no encontrado"));
        assertTrue(duplicate.getMessage().contains("ya existe"));
    }

    @Test
    @DisplayName("Should handle multiple instances with different messages")
    void testMultipleInstances() {
        // When
        ResourceNotFoundException ex1 = new ResourceNotFoundException("User", "id", 1);
        ResourceNotFoundException ex2 = new ResourceNotFoundException("Product", "sku", "ABC");
        ResourceNotFoundException ex3 = new ResourceNotFoundException("Custom message");

        // Then
        assertNotEquals(ex1.getMessage(), ex2.getMessage());
        assertNotEquals(ex2.getMessage(), ex3.getMessage());
        assertTrue(ex1.getMessage().contains("User"));
        assertTrue(ex2.getMessage().contains("Product"));
        assertEquals("Custom message", ex3.getMessage());
    }
}
