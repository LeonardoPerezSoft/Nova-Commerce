package com.novacommerce.order_service.adapter.in.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para JwtAuthenticationFilter.
 * Valida el filtro de autenticación JWT en peticiones HTTP.
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenValidator tokenValidator;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter authenticationFilter;

    @BeforeEach
    void setUp() {
        // Limpiar el contexto de seguridad antes de cada test
        SecurityContextHolder.clearContext();
    }

    @Test
    void givenValidBearerToken_whenDoFilterInternal_thenSetsAuthentication() throws ServletException, IOException {
        // GIVEN - Request con token Bearer válido
        String token = "valid.jwt.token";
        String authHeader = "Bearer " + token;
        String username = "john.doe";
        List<String> authorities = List.of("ROLE_USER", "ROLE_ADMIN");

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenValidator.isValid(token)).thenReturn(true);
        when(tokenValidator.getUsername(token)).thenReturn(username);
        when(tokenValidator.getAuthorities(token)).thenReturn(authorities);

        // WHEN - Procesar filtro
        authenticationFilter.doFilterInternal(request, response, filterChain);

        // THEN - Debe establecer autenticación en el contexto
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(username, authentication.getPrincipal());
        assertEquals(2, authentication.getAuthorities().size());
        assertTrue(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));

        verify(tokenValidator).isValid(token);
        verify(tokenValidator).getUsername(token);
        verify(tokenValidator).getAuthorities(token);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void givenInvalidBearerToken_whenDoFilterInternal_thenDoesNotSetAuthentication() throws ServletException, IOException {
        // GIVEN - Request con token Bearer inválido
        String token = "invalid.jwt.token";
        String authHeader = "Bearer " + token;

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenValidator.isValid(token)).thenReturn(false);

        // WHEN - Procesar filtro
        authenticationFilter.doFilterInternal(request, response, filterChain);

        // THEN - No debe establecer autenticación
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(tokenValidator).isValid(token);
        verify(tokenValidator, never()).getUsername(anyString());
        verify(tokenValidator, never()).getAuthorities(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void givenNoAuthorizationHeader_whenDoFilterInternal_thenDoesNotSetAuthentication() throws ServletException, IOException {
        // GIVEN - Request sin header Authorization
        when(request.getHeader("Authorization")).thenReturn(null);

        // WHEN - Procesar filtro
        authenticationFilter.doFilterInternal(request, response, filterChain);

        // THEN - No debe intentar validar token ni establecer autenticación
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(tokenValidator, never()).isValid(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void givenAuthorizationHeaderWithoutBearer_whenDoFilterInternal_thenDoesNotSetAuthentication() throws ServletException, IOException {
        // GIVEN - Request con header Authorization pero sin prefijo Bearer
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        // WHEN - Procesar filtro
        authenticationFilter.doFilterInternal(request, response, filterChain);

        // THEN - No debe procesar el token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(tokenValidator, never()).isValid(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void givenValidTokenWithNoAuthorities_whenDoFilterInternal_thenSetsAuthenticationWithEmptyAuthorities() throws ServletException, IOException {
        // GIVEN - Request con token válido pero sin authorities
        String token = "valid.jwt.token";
        String authHeader = "Bearer " + token;
        String username = "jane.doe";
        List<String> emptyAuthorities = List.of();

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenValidator.isValid(token)).thenReturn(true);
        when(tokenValidator.getUsername(token)).thenReturn(username);
        when(tokenValidator.getAuthorities(token)).thenReturn(emptyAuthorities);

        // WHEN - Procesar filtro
        authenticationFilter.doFilterInternal(request, response, filterChain);

        // THEN - Debe establecer autenticación con authorities vacías
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(username, authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().isEmpty());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void givenValidTokenWithSingleAuthority_whenDoFilterInternal_thenSetsAuthenticationWithSingleAuthority() throws ServletException, IOException {
        // GIVEN - Request con token válido y una sola authority
        String token = "valid.jwt.token";
        String authHeader = "Bearer " + token;
        String username = "bob.smith";
        List<String> authorities = List.of("ROLE_USER");

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenValidator.isValid(token)).thenReturn(true);
        when(tokenValidator.getUsername(token)).thenReturn(username);
        when(tokenValidator.getAuthorities(token)).thenReturn(authorities);

        // WHEN - Procesar filtro
        authenticationFilter.doFilterInternal(request, response, filterChain);

        // THEN - Debe establecer autenticación con una authority
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(1, authentication.getAuthorities().size());
        assertTrue(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void givenBearerTokenOnly_whenDoFilterInternal_thenHandlesEmptyToken() throws ServletException, IOException {
        // GIVEN - Request con "Bearer " pero sin token
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        // WHEN - Procesar filtro
        authenticationFilter.doFilterInternal(request, response, filterChain);

        // THEN - Debe intentar validar (el validador rechazará el token vacío)
        verify(tokenValidator).isValid("");
        verify(filterChain).doFilter(request, response);
    }
}
