package com.novacommerce.auth_service.web.api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RefreshTokenRequest DTO Tests")
class RefreshTokenRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create valid RefreshTokenRequest")
    void testValidRefreshTokenRequest() {
        // Given & When
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");

        // Then
        assertNotNull(request);
        assertEquals("valid-refresh-token", request.refreshToken());
        
        Set<ConstraintViolation<RefreshTokenRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should reject null refreshToken")
    void testNullRefreshToken() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest(null);

        // When
        Set<ConstraintViolation<RefreshTokenRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("obligatorio"));
    }

    @Test
    @DisplayName("Should reject empty refreshToken")
    void testEmptyRefreshToken() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("");

        // When
        Set<ConstraintViolation<RefreshTokenRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Should reject blank refreshToken")
    void testBlankRefreshToken() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("   ");

        // When
        Set<ConstraintViolation<RefreshTokenRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Should be immutable record")
    void testImmutability() {
        // Given
        RefreshTokenRequest request1 = new RefreshTokenRequest("token123");
        RefreshTokenRequest request2 = new RefreshTokenRequest("token123");

        // Then
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }
}
