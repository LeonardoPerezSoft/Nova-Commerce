package com.novacommerce.gateway.routing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RouteConstantsTest {

    @Test
    void testHeaderConstantsValues() {
        assertEquals("Authorization", RouteConstants.HEADER_AUTHORIZATION);
        assertEquals("X-Username", RouteConstants.HEADER_USERNAME);
        assertEquals("X-Authorities", RouteConstants.HEADER_AUTHORITIES);
        assertEquals("Bearer ", RouteConstants.BEARER_PREFIX);
    }

    @Test
    void testAuthServiceConstants() {
        assertEquals("auth-service", RouteConstants.AUTH_SERVICE_ID);
        assertEquals("/api/auth/**", RouteConstants.AUTH_SERVICE_PATH);
    }

    @Test
    void testConstantsNotNull() {
        assertNotNull(RouteConstants.HEADER_AUTHORIZATION);
        assertNotNull(RouteConstants.HEADER_USERNAME);
        assertNotNull(RouteConstants.HEADER_AUTHORITIES);
        assertNotNull(RouteConstants.BEARER_PREFIX);
        assertNotNull(RouteConstants.AUTH_SERVICE_ID);
        assertNotNull(RouteConstants.AUTH_SERVICE_PATH);
    }

    @Test
    void testConstantsNotEmpty() {
        assertFalse(RouteConstants.HEADER_AUTHORIZATION.isEmpty());
        assertFalse(RouteConstants.HEADER_USERNAME.isEmpty());
        assertFalse(RouteConstants.HEADER_AUTHORITIES.isEmpty());
        assertFalse(RouteConstants.BEARER_PREFIX.isEmpty());
        assertFalse(RouteConstants.AUTH_SERVICE_ID.isEmpty());
        assertFalse(RouteConstants.AUTH_SERVICE_PATH.isEmpty());
    }

    @Test
    void testConstructorThrowsException() {
        try {
            java.lang.reflect.Constructor<?> constructor = RouteConstants.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
            fail("Should throw UnsupportedOperationException");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof UnsupportedOperationException,
                "Constructor should throw UnsupportedOperationException");
        }
    }

    @Test
    void testBearerPrefixFormat() {
        assertTrue(RouteConstants.BEARER_PREFIX.startsWith("Bearer "));
        assertEquals("Bearer ", RouteConstants.BEARER_PREFIX);
    }
}
