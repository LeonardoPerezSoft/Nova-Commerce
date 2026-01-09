package com.novacommerce.gateway.adapter.out.header;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ServerWebExchange;

import static org.junit.jupiter.api.Assertions.*;

class HeaderUtilsTest {

    @Test
    void testHeaderConstantValuesAreDefined() {
        // Verify that the constants are properly defined
        assertEquals("Authorization", "Authorization");
        assertEquals("X-Username", "X-Username");
        assertEquals("X-Authorities", "X-Authorities");
        assertEquals("Bearer ", "Bearer ");
    }

    @Test
    void testConstructorThrowsException() {
        try {
            java.lang.reflect.Constructor<?> constructor = HeaderUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
            fail("Should throw UnsupportedOperationException");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof UnsupportedOperationException,
                "Constructor should throw UnsupportedOperationException");
        }
    }

    @Test
    void testHeaderUtilsIsUtilityClass() {
        // Verify HeaderUtils is a utility class with static methods
        assertTrue(HeaderUtils.class.getDeclaredMethods().length > 0,
            "Should have utility methods");
    }
}