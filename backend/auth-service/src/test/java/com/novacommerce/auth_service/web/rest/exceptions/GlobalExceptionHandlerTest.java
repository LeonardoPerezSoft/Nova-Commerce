package com.novacommerce.auth_service.web.rest.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
    }

    @Test
    @DisplayName("Debe manejar ResourceNotFoundException")
    void testHandleResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Usuario", "id", 123L);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(
            exception, webRequest
        );

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("Usuario no encontrado con id = 123", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Debe manejar DuplicateResourceException")
    void testHandleDuplicateResourceException() {
        DuplicateResourceException exception = new DuplicateResourceException("Usuario", "email", "test@example.com");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDuplicateResourceException(
            exception, webRequest
        );

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Conflict", response.getBody().getError());
        assertEquals("Usuario con email = test@example.com ya existe", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Debe manejar InvalidCredentialsException")
    void testHandleInvalidCredentialsException() {
        InvalidCredentialsException exception = new InvalidCredentialsException("Credenciales inválidas");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidCredentialsException(
            exception, webRequest
        );

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Unauthorized", response.getBody().getError());
        assertEquals("Credenciales inválidas", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Debe manejar InvalidTokenException")
    void testHandleInvalidTokenException() {
        InvalidTokenException exception = new InvalidTokenException("Token expirado");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidTokenException(
            exception, webRequest
        );

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Unauthorized", response.getBody().getError());
        assertEquals("Token expirado", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Debe manejar MethodArgumentNotValidException")
    void testHandleMethodArgumentNotValidException() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError fieldError1 = new FieldError("loginRequest", "username", "no debe estar vacío");
        FieldError fieldError2 = new FieldError("loginRequest", "password", "no debe estar vacío");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodArgumentNotValid(
            exception, webRequest
        );

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Validación de entrada fallida", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
        assertNotNull(response.getBody().getFieldErrors());
        assertEquals(2, response.getBody().getFieldErrors().size());
        assertEquals("no debe estar vacío", response.getBody().getFieldErrors().get("username"));
        assertEquals("no debe estar vacío", response.getBody().getFieldErrors().get("password"));
    }

    @Test
    @DisplayName("Debe manejar AccessDeniedException")
    void testHandleAccessDeniedException() {
        AccessDeniedException exception = new AccessDeniedException("Acceso denegado");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDeniedException(
            exception, webRequest
        );

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(403, response.getBody().getStatus());
        assertEquals("Forbidden", response.getBody().getError());
        assertEquals("No tiene permisos para acceder a este recurso", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Debe manejar AuthenticationException")
    void testHandleAuthenticationException() {
        AuthenticationException exception = mock(AuthenticationException.class);
        when(exception.getMessage()).thenReturn("Usuario no autenticado");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAuthenticationException(
            exception, webRequest
        );

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Unauthorized", response.getBody().getError());
        assertEquals("No autenticado", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Debe manejar Exception genérica")
    void testHandleGlobalException() {
        Exception exception = new RuntimeException("Error inesperado");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(
            exception, webRequest
        );

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Ha ocurrido un error inesperado", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Debe manejar WebRequest sin uri prefix")
    void testWebRequestWithoutUriPrefix() {
        when(webRequest.getDescription(false)).thenReturn("/api/test/path");
        
        InvalidTokenException exception = new InvalidTokenException("Token inválido");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidTokenException(
            exception, webRequest
        );

        assertEquals("/api/test/path", response.getBody().getPath());
    }

    @Test
    @DisplayName("Debe manejar MethodArgumentNotValidException con error de validación vacío")
    void testMethodArgumentNotValidWithNoErrors() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of());

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodArgumentNotValid(
            exception, webRequest
        );

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().getFieldErrors());
        assertTrue(response.getBody().getFieldErrors().isEmpty());
    }

    @Test
    @DisplayName("Debe manejar múltiples errores en el mismo campo")
    void testMultipleErrorsSameField() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError fieldError1 = new FieldError("loginRequest", "email", "no debe estar vacío");
        FieldError fieldError2 = new FieldError("loginRequest", "email", "debe ser un email válido");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodArgumentNotValid(
            exception, webRequest
        );

        assertNotNull(response);
        // El último error sobrescribe el primero
        assertEquals("debe ser un email válido", response.getBody().getFieldErrors().get("email"));
    }
}
