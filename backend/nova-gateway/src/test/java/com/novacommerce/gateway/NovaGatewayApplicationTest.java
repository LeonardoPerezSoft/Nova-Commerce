package com.novacommerce.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NovaGatewayApplicationTest {

    @Test
    void testApplicationContextLoads() {
        // This test passes if the Spring context loads successfully
        assertTrue(true, "Application context should load successfully");
    }

    @Test
    void testMainMethodExists() {
        assertDoesNotThrow(() -> {
            // Check that the main method exists and can be referenced
            NovaGatewayApplication.class.getDeclaredMethod("main", String[].class);
        }, "Main method should exist");
    }

    @Test
    void testSpringBootApplicationAnnotation() {
        assertTrue(NovaGatewayApplication.class.isAnnotationPresent(
            org.springframework.boot.autoconfigure.SpringBootApplication.class),
            "Should have @SpringBootApplication annotation");
    }
}
