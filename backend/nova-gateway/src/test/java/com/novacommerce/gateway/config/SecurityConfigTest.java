package com.novacommerce.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.AntPathMatcher;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private AntPathMatcher antPathMatcher;

    @Test
    void testAntPathMatcherBeanCreation() {
        assertNotNull(antPathMatcher, "AntPathMatcher bean should not be null");
        assertNotNull(securityConfig, "SecurityConfig should not be null");
    }

    @Test
    void testAntPathMatcherPattern() {
        assertTrue(antPathMatcher.match("/api/users/**", "/api/users/123"));
        assertTrue(antPathMatcher.match("/api/users/**", "/api/users/123/profile"));
        assertFalse(antPathMatcher.match("/api/users/**", "/api/products/123"));
    }

    @Test
    void testAntPathMatcherExactMatch() {
        assertTrue(antPathMatcher.match("/api/auth/login", "/api/auth/login"));
        assertFalse(antPathMatcher.match("/api/auth/login", "/api/auth/logout"));
    }

    @Test
    void testAntPathMatcherSingleWildcard() {
        assertTrue(antPathMatcher.match("/api/*/123", "/api/users/123"));
        assertTrue(antPathMatcher.match("/api/*/123", "/api/products/123"));
    }

    @Test
    void testAntPathMatcherComplexPattern() {
        assertTrue(antPathMatcher.match("/api/**/status", "/api/users/status"));
        assertTrue(antPathMatcher.match("/api/**/status", "/api/users/profile/status"));
        assertFalse(antPathMatcher.match("/api/**/status", "/api/users/123"));
    }
}
