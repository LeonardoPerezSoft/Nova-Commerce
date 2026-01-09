package com.novacommerce.user_service.config.security;

import com.novacommerce.user_service.adapter.in.filter.InternalApiKeyFilter;
import com.novacommerce.user_service.adapter.in.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("SecurityConfig Tests")
class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private InternalApiKeyFilter internalApiKeyFilter;

    @Test
    @DisplayName("Should create SecurityConfig bean")
    void testSecurityConfigBean() {
        assertNotNull(securityConfig);
    }

    @Test
    @DisplayName("Should create PasswordEncoder bean")
    void testPasswordEncoderBean() {
        assertNotNull(passwordEncoder);
    }

    @Test
    @DisplayName("Should create SecurityFilterChain bean")
    void testSecurityFilterChainBean() {
        assertNotNull(securityFilterChain);
    }

    @Test
    @DisplayName("Should encode password correctly")
    void testPasswordEncoding() {
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    @DisplayName("Should not match incorrect password")
    void testPasswordMismatch() {
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertFalse(passwordEncoder.matches("wrongpassword", encodedPassword));
    }

    @Test
    @DisplayName("Should have JwtAuthenticationFilter bean")
    void testJwtFilterBean() {
        assertNotNull(jwtAuthenticationFilter);
    }

    @Test
    @DisplayName("Should have InternalApiKeyFilter bean")
    void testInternalApiKeyFilterBean() {
        assertNotNull(internalApiKeyFilter);
    }

    @Test
    @DisplayName("Should encode different passwords differently")
    void testDifferentPasswordsEncodeDifferently() {
        String password1 = "password1";
        String password2 = "password2";

        String encoded1 = passwordEncoder.encode(password1);
        String encoded2 = passwordEncoder.encode(password2);

        assertNotEquals(encoded1, encoded2);
    }

    @Test
    @DisplayName("Should encode same password differently each time")
    void testSamePasswordEncodedDifferently() {
        String password = "password123";

        String encoded1 = passwordEncoder.encode(password);
        String encoded2 = passwordEncoder.encode(password);

        assertNotEquals(encoded1, encoded2);
        assertTrue(passwordEncoder.matches(password, encoded1));
        assertTrue(passwordEncoder.matches(password, encoded2));
    }

    @Test
    @DisplayName("Should handle empty password")
    void testEmptyPassword() {
        String emptyPassword = "";
        String encoded = passwordEncoder.encode(emptyPassword);

        assertNotNull(encoded);
        assertTrue(passwordEncoder.matches(emptyPassword, encoded));
    }
}
