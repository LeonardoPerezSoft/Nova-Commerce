package com.novacommerce.auth_service.adapter.in.web;

import com.novacommerce.auth_service.application.port.in.AuthenticateUserUseCase;
import com.novacommerce.auth_service.application.port.in.RefreshTokenUseCase;
import com.novacommerce.auth_service.application.port.in.ValidateTokenUseCase;
import com.novacommerce.auth_service.web.api.dto.request.LoginRequest;
import com.novacommerce.auth_service.web.api.dto.request.RefreshTokenRequest;
import com.novacommerce.auth_service.web.api.dto.response.LoginResponse;
import com.novacommerce.auth_service.web.api.dto.response.TokenValidationResponse;
import com.novacommerce.auth_service.config.security.jwt.JwtTokenProvider;
import com.novacommerce.auth_service.web.rest.exceptions.InvalidCredentialsException;
import com.novacommerce.auth_service.web.rest.exceptions.InvalidTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthRestController.class, excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = com.novacommerce.auth_service.config.security.jwt.JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthRestController Tests")
class AuthRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticateUserUseCase authenticateUserUseCase;

    @MockBean
    private RefreshTokenUseCase refreshTokenUseCase;

    @MockBean
    private ValidateTokenUseCase validateTokenUseCase;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("Should login successfully")
    void testLoginSuccess() throws Exception {
        // Given
        LoginResponse loginResponse = new LoginResponse(
            "access-token",
            "refresh-token",
            "Bearer",
            3600L,
            "testuser",
            List.of("ROLE_USER")
        );

        when(authenticateUserUseCase.authenticate(any(LoginRequest.class)))
            .thenReturn(loginResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userIdentifier\":\"testuser\",\"password\":\"password123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.access_token").value("access-token"))
            .andExpect(jsonPath("$.refresh_token").value("refresh-token"))
            .andExpect(jsonPath("$.token_type").value("Bearer"))
            .andExpect(jsonPath("$.expires_in").value(3600))
            .andExpect(jsonPath("$.username").value("testuser"));

        verify(authenticateUserUseCase, times(1)).authenticate(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when login request is invalid")
    void testLoginInvalidRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userIdentifier\":\"\",\"password\":\"\"}"))
            .andExpect(status().isBadRequest());

        verify(authenticateUserUseCase, never()).authenticate(any());
    }

    @Test
    @DisplayName("Should return 401 when credentials are invalid")
    void testLoginInvalidCredentials() throws Exception {
        // Given
        when(authenticateUserUseCase.authenticate(any(LoginRequest.class)))
            .thenThrow(new InvalidCredentialsException("Credenciales inválidas"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userIdentifier\":\"testuser\",\"password\":\"wrongpassword\"}"))
            .andExpect(status().isUnauthorized());

        verify(authenticateUserUseCase, times(1)).authenticate(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void testRefreshTokenSuccess() throws Exception {
        // Given
        LoginResponse loginResponse = new LoginResponse(
            "new-access-token",
            "refresh-token",
            "Bearer",
            3600L,
            "testuser",
            List.of()
        );

        when(refreshTokenUseCase.refreshToken(any(RefreshTokenRequest.class)))
            .thenReturn(loginResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"valid-refresh-token\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.access_token").value("new-access-token"))
            .andExpect(jsonPath("$.refresh_token").value("refresh-token"));

        verify(refreshTokenUseCase, times(1)).refreshToken(any(RefreshTokenRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when refresh token request is invalid")
    void testRefreshTokenInvalidRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"\"}"))
            .andExpect(status().isBadRequest());

        verify(refreshTokenUseCase, never()).refreshToken(any());
    }

    @Test
    @DisplayName("Should return 401 when refresh token is invalid")
    void testRefreshTokenInvalid() throws Exception {
        // Given
        when(refreshTokenUseCase.refreshToken(any(RefreshTokenRequest.class)))
            .thenThrow(new InvalidTokenException("Token inválido"));

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"invalid-token\"}"))
            .andExpect(status().isUnauthorized());

        verify(refreshTokenUseCase, times(1)).refreshToken(any(RefreshTokenRequest.class));
    }

    @Test
    @DisplayName("Should validate token successfully")
    void testValidateTokenSuccess() throws Exception {
        // Given
        TokenValidationResponse validationResponse = new TokenValidationResponse(
            true,
            "testuser",
            "ROLE_USER"
        );

        when(validateTokenUseCase.validateToken(anyString()))
            .thenReturn(validationResponse);

        // When & Then
        mockMvc.perform(get("/api/auth/validate")
                .header("Authorization", "Bearer valid-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(true))
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.authorities").value("ROLE_USER"));

        verify(validateTokenUseCase, times(1)).validateToken("valid-token");
    }

    @Test
    @DisplayName("Should validate token without Bearer prefix")
    void testValidateTokenWithoutBearer() throws Exception {
        // Given
        TokenValidationResponse validationResponse = new TokenValidationResponse(
            false,
            null,
            null
        );

        when(validateTokenUseCase.validateToken(null))
            .thenReturn(validationResponse);

        // When & Then
        mockMvc.perform(get("/api/auth/validate"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(false));

        verify(validateTokenUseCase, times(1)).validateToken(null);
    }

    @Test
    @DisplayName("Should return invalid when token is not provided")
    void testValidateTokenNotProvided() throws Exception {
        // Given
        TokenValidationResponse validationResponse = new TokenValidationResponse(
            false,
            null,
            null
        );

        when(validateTokenUseCase.validateToken(null))
            .thenReturn(validationResponse);

        // When & Then
        mockMvc.perform(get("/api/auth/validate"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(false))
            .andExpect(jsonPath("$.username").doesNotExist())
            .andExpect(jsonPath("$.authorities").doesNotExist());
    }
}
