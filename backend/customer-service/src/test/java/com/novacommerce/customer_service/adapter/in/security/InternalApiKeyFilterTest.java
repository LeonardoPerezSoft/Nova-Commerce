package com.novacommerce.customer_service.adapter.in.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("InternalApiKeyFilterTest")
class InternalApiKeyFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private InternalApiKeyFilter filter;

    @BeforeEach
    void setUp() {
        filter = new InternalApiKeyFilter("nova-internal-service-key-2024");
    }

    @Test
    @DisplayName("givenValidInternalApiKey_whenDoFilter_thenContinueChain")
    void givenValidInternalApiKey_whenDoFilter_thenContinueChain() throws ServletException, IOException {
        // GIVEN
        when(request.getHeader("X-Internal-API-Key"))
            .thenReturn("nova-internal-service-key-2024");

        // WHEN
        filter.doFilterInternal(request, response, filterChain);

        // THEN
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(any(Integer.TYPE));
    }

    @Test
    @DisplayName("givenInvalidInternalApiKey_whenDoFilter_thenReturnForbidden")
    void givenInvalidInternalApiKey_whenDoFilter_thenReturnForbidden() throws ServletException, IOException {
        // GIVEN
        when(request.getHeader("X-Internal-API-Key"))
            .thenReturn("wrong-key");

        // WHEN
        filter.doFilterInternal(request, response, filterChain);

        // THEN
        verify(response).setStatus(HttpStatus.FORBIDDEN.value());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("givenMissingInternalApiKey_whenDoFilter_thenReturnForbidden")
    void givenMissingInternalApiKey_whenDoFilter_thenReturnForbidden() throws ServletException, IOException {
        // GIVEN
        when(request.getHeader("X-Internal-API-Key")).thenReturn(null);

        // WHEN
        filter.doFilterInternal(request, response, filterChain);

        // THEN
        verify(response).setStatus(HttpStatus.FORBIDDEN.value());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("givenPublicEndpoint_whenShouldNotFilter_thenSkip")
    void givenPublicEndpoint_whenShouldNotFilter_thenSkip() throws ServletException {
        // GIVEN
        when(request.getRequestURI()).thenReturn("/api/public/health");

        // WHEN
        boolean shouldSkip = filter.shouldNotFilter(request);

        // THEN
        assertTrue(shouldSkip);
    }

    @Test
    @DisplayName("givenSwaggerEndpoint_whenShouldNotFilter_thenSkip")
    void givenSwaggerEndpoint_whenShouldNotFilter_thenSkip() throws ServletException {
        // GIVEN
        when(request.getRequestURI()).thenReturn("/swagger-ui.html");

        // WHEN
        boolean shouldSkip = filter.shouldNotFilter(request);

        // THEN
        assertTrue(shouldSkip);
    }

    @Test
    @DisplayName("givenActuatorEndpoint_whenShouldNotFilter_thenSkip")
    void givenActuatorEndpoint_whenShouldNotFilter_thenSkip() throws ServletException {
        // GIVEN
        when(request.getRequestURI()).thenReturn("/actuator/health");

        // WHEN
        boolean shouldSkip = filter.shouldNotFilter(request);

        // THEN
        assertTrue(shouldSkip);
    }

    @Test
    @DisplayName("givenInternalEndpointWithValidKey_whenShouldNotFilter_thenReturnFalse")
    void givenInternalEndpointWithValidKey_whenShouldNotFilter_thenReturnFalse() throws ServletException {
        // GIVEN
        when(request.getRequestURI()).thenReturn("/internal/customers");

        // WHEN
        boolean shouldNotFilter = filter.shouldNotFilter(request);

        // THEN
        // Should NOT skip the filter for /internal paths
        assert !shouldNotFilter;
    }

    @Test
    @DisplayName("givenEmptyApiKeyHeader_whenDoFilter_thenReturnForbidden")
    void givenEmptyApiKeyHeader_whenDoFilter_thenReturnForbidden() throws ServletException, IOException {
        // GIVEN
        when(request.getHeader("X-Internal-API-Key")).thenReturn("");

        // WHEN
        filter.doFilterInternal(request, response, filterChain);

        // THEN
        verify(response).setStatus(HttpStatus.FORBIDDEN.value());
        verify(filterChain, never()).doFilter(request, response);
    }
}
