package com.novacommerce.auth_service.config.security.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtConstants Tests")
class JwtConstantsTest {

    @Test
    @DisplayName("Debe tener constructor privado")
    void testPrivateConstructor() throws NoSuchMethodException {
        Constructor<JwtConstants> constructor = JwtConstants.class.getDeclaredConstructor();
        assertFalse(constructor.canAccess(null));
        constructor.setAccessible(true);
        
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(AssertionError.class, exception.getCause());
        assertEquals("No se puede instanciar esta clase de utilidad", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Debe verificar constantes de headers y prefijos")
    void testHeaderConstants() {
        assertEquals("Authorization", JwtConstants.AUTHORIZATION_HEADER);
        assertEquals("Bearer ", JwtConstants.BEARER_PREFIX);
        assertEquals(7, JwtConstants.TOKEN_START_INDEX);
    }

    @Test
    @DisplayName("Debe verificar constantes de claims")
    void testClaimConstants() {
        assertEquals("authorities", JwtConstants.AUTHORITIES_CLAIM);
    }

    @Test
    @DisplayName("Debe verificar constantes de mensajes de error")
    void testErrorMessageConstants() {
        assertEquals("Token JWT inválido", JwtConstants.INVALID_TOKEN_MESSAGE);
        assertEquals("Token JWT expirado", JwtConstants.EXPIRED_TOKEN_MESSAGE);
        assertEquals("Token JWT no soportado", JwtConstants.UNSUPPORTED_TOKEN_MESSAGE);
        assertEquals("Token JWT con claims vacíos", JwtConstants.EMPTY_CLAIMS_MESSAGE);
    }

    @Test
    @DisplayName("Debe verificar que todas las constantes no sean nulas")
    void testNonNullConstants() {
        assertNotNull(JwtConstants.AUTHORIZATION_HEADER);
        assertNotNull(JwtConstants.BEARER_PREFIX);
        assertNotNull(JwtConstants.AUTHORITIES_CLAIM);
        assertNotNull(JwtConstants.INVALID_TOKEN_MESSAGE);
        assertNotNull(JwtConstants.EXPIRED_TOKEN_MESSAGE);
        assertNotNull(JwtConstants.UNSUPPORTED_TOKEN_MESSAGE);
        assertNotNull(JwtConstants.EMPTY_CLAIMS_MESSAGE);
    }

    @Test
    @DisplayName("Debe verificar que TOKEN_START_INDEX coincida con longitud de BEARER_PREFIX")
    void testTokenStartIndexConsistency() {
        assertEquals(JwtConstants.BEARER_PREFIX.length(), JwtConstants.TOKEN_START_INDEX);
    }
}
