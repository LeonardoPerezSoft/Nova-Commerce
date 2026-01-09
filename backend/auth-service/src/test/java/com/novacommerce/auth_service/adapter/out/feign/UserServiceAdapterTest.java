package com.novacommerce.auth_service.adapter.out.feign;

import com.novacommerce.auth_service.client.UserServiceClient;
import com.novacommerce.auth_service.client.dto.UserValidationRequest;
import com.novacommerce.auth_service.client.dto.UserValidationResponse;

import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceAdapter Tests")
class UserServiceAdapterTest {

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private UserServiceAdapter userServiceAdapter;

    private String internalApiKey = "test-api-key";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userServiceAdapter, "internalApiKey", internalApiKey);
    }

    @Test
    @DisplayName("Should validate credentials successfully")
    void testValidateCredentialsSuccess() {
        // Given
        UserValidationRequest request = new UserValidationRequest("testuser", "password123");
        
        UserValidationResponse expectedResponse = new UserValidationResponse(
            "testuser",
            "test@email.com",
            true,
            false,
            Set.of("ADMIN"),
            Set.of("USER_READ", "USER_WRITE")
        );

        when(userServiceClient.validateCredentials(any(UserValidationRequest.class), anyString()))
            .thenReturn(expectedResponse);

        // When
        UserValidationResponse response = userServiceAdapter.validateCredentials(request);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse.username(), response.username());
        assertEquals(expectedResponse.email(), response.email());
        assertEquals(expectedResponse.enabled(), response.enabled());
        assertEquals(expectedResponse.locked(), response.locked());
        assertEquals(expectedResponse.roles(), response.roles());
        assertEquals(expectedResponse.permissions(), response.permissions());
        
        verify(userServiceClient, times(1)).validateCredentials(request, internalApiKey);
    }

    @Test
    @DisplayName("Should propagate Feign exception when credentials are invalid")
    void testValidateCredentialsInvalid() {
        // Given
        UserValidationRequest request = new UserValidationRequest("testuser", "wrongpassword");
        
        when(userServiceClient.validateCredentials(any(UserValidationRequest.class), anyString()))
            .thenThrow(mock(FeignException.Unauthorized.class));

        // When & Then
        assertThrows(FeignException.Unauthorized.class, () -> 
            userServiceAdapter.validateCredentials(request)
        );
        
        verify(userServiceClient, times(1)).validateCredentials(request, internalApiKey);
    }

    @Test
    @DisplayName("Should propagate Feign exception when user service is unavailable")
    void testValidateCredentialsServiceUnavailable() {
        // Given
        UserValidationRequest request = new UserValidationRequest("testuser", "password123");
        
        when(userServiceClient.validateCredentials(any(UserValidationRequest.class), anyString()))
            .thenThrow(mock(FeignException.ServiceUnavailable.class));

        // When & Then
        assertThrows(FeignException.ServiceUnavailable.class, () -> 
            userServiceAdapter.validateCredentials(request)
        );
        
        verify(userServiceClient, times(1)).validateCredentials(request, internalApiKey);
    }

    @Test
    @DisplayName("Should use correct internal API key")
    void testUsesCorrectApiKey() {
        // Given
        String customApiKey = "custom-key-12345";
        ReflectionTestUtils.setField(userServiceAdapter, "internalApiKey", customApiKey);
        
        UserValidationRequest request = new UserValidationRequest("testuser", "password123");
        
        UserValidationResponse expectedResponse = new UserValidationResponse(
            "testuser",
            "test@email.com",
            true,
            false,
            Set.of("USER"),
            Set.of()
        );

        when(userServiceClient.validateCredentials(any(UserValidationRequest.class), eq(customApiKey)))
            .thenReturn(expectedResponse);

        // When
        userServiceAdapter.validateCredentials(request);

        // Then
        verify(userServiceClient).validateCredentials(any(UserValidationRequest.class), eq(customApiKey));
    }

    @Test
    @DisplayName("Should handle user with no roles")
    void testValidateCredentialsNoRoles() {
        // Given
        UserValidationRequest request = new UserValidationRequest("testuser", "password123");
        
        UserValidationResponse expectedResponse = new UserValidationResponse(
            "testuser",
            "test@email.com",
            true,
            false,
            Set.<String>of(),
            Set.<String>of()
        );

        when(userServiceClient.validateCredentials(any(UserValidationRequest.class), anyString()))
            .thenReturn(expectedResponse);

        // When
        UserValidationResponse response = userServiceAdapter.validateCredentials(request);

        // Then
        assertNotNull(response);
        assertTrue(response.roles().isEmpty());
        assertTrue(response.permissions().isEmpty());
    }

    @Test
    @DisplayName("Should handle disabled user")
    void testValidateCredentialsDisabledUser() {
        // Given
        UserValidationRequest request = new UserValidationRequest("testuser", "password123");
        
        UserValidationResponse expectedResponse = new UserValidationResponse(
            "testuser",
            "test@email.com",
            false, // disabled
            false,
            Set.of("USER"),
            Set.of()
        );

        when(userServiceClient.validateCredentials(any(UserValidationRequest.class), anyString()))
            .thenReturn(expectedResponse);

        // When
        UserValidationResponse response = userServiceAdapter.validateCredentials(request);

        // Then
        assertNotNull(response);
        assertFalse(response.enabled());
    }

    @Test
    @DisplayName("Should handle locked user")
    void testValidateCredentialsLockedUser() {
        // Given
        UserValidationRequest request = new UserValidationRequest("testuser", "password123");
        
        UserValidationResponse expectedResponse = new UserValidationResponse(
            "testuser",
            "test@email.com",
            true,
            true, // locked
            Set.of("USER"),
            Set.of()
        );

        when(userServiceClient.validateCredentials(any(UserValidationRequest.class), anyString()))
            .thenReturn(expectedResponse);

        // When
        UserValidationResponse response = userServiceAdapter.validateCredentials(request);

        // Then
        assertNotNull(response);
        assertTrue(response.locked());
    }
}
