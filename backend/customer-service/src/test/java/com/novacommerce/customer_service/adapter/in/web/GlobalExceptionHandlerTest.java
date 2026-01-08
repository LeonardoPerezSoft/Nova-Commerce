package com.novacommerce.customer_service.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandlerTest")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Test
    @DisplayName("givenIllegalArgumentException_whenHandleIllegalArgument_thenReturnBadRequest")
    void givenIllegalArgumentException_whenHandleIllegalArgument_thenReturnBadRequest() {
        // GIVEN
        String errorMessage = "El correo ya está registrado";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // WHEN
        ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgument(exception);

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().get("error"));
    }

    @Test
    @DisplayName("givenIllegalStateException_whenHandleIllegalState_thenReturnConflict")
    void givenIllegalStateException_whenHandleIllegalState_thenReturnConflict() {
        // GIVEN
        String errorMessage = "Cliente bloqueado, no puede actualizarse";
        IllegalStateException exception = new IllegalStateException(errorMessage);

        // WHEN
        ResponseEntity<Map<String, Object>> response = handler.handleIllegalState(exception);

        // THEN
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().get("error"));
    }

    @Test
    @DisplayName("givenMethodArgumentNotValidException_whenHandleValidation_thenReturnBadRequest")
    void givenMethodArgumentNotValidException_whenHandleValidation_thenReturnBadRequest() {
        // GIVEN
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("customer", "firstName", "debe ser no vacío");
        FieldError fieldError2 = new FieldError("customer", "email", "debe ser un email válido");

        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(fieldError1);
        fieldErrors.add(fieldError2);

        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // WHEN
        ResponseEntity<Map<String, Object>> response = handler.handleValidation(exception);

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validación fallida", response.getBody().get("error"));
        assertNotNull(response.getBody().get("details"));
    }

    @Test
    @DisplayName("givenIllegalArgumentWithDifferentMessage_whenHandleIllegalArgument_thenReturnSameMessage")
    void givenIllegalArgumentWithDifferentMessage_whenHandleIllegalArgument_thenReturnSameMessage() {
        // GIVEN
        String errorMessage = "Cliente no encontrado";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // WHEN
        ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgument(exception);

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().get("error"));
    }

    @Test
    @DisplayName("givenIllegalStateWithDifferentMessage_whenHandleIllegalState_thenReturnSameMessage")
    void givenIllegalStateWithDifferentMessage_whenHandleIllegalState_thenReturnSameMessage() {
        // GIVEN
        String errorMessage = "Cliente bloqueado, no puede eliminarse";
        IllegalStateException exception = new IllegalStateException(errorMessage);

        // WHEN
        ResponseEntity<Map<String, Object>> response = handler.handleIllegalState(exception);

        // THEN
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().get("error"));
    }

    @Test
    @DisplayName("givenValidationExceptionWithNoFieldErrors_whenHandleValidation_thenReturnEmptyDetails")
    void givenValidationExceptionWithNoFieldErrors_whenHandleValidation_thenReturnEmptyDetails() {
        // GIVEN
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(new ArrayList<>());

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // WHEN
        ResponseEntity<Map<String, Object>> response = handler.handleValidation(exception);

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().get("details"));
    }

    @Test
    @DisplayName("givenIllegalArgumentException_whenHandled_thenResponseBodyNotNull")
    void givenIllegalArgumentException_whenHandled_thenResponseBodyNotNull() {
        // GIVEN
        IllegalArgumentException exception = new IllegalArgumentException("Test error");

        // WHEN
        ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgument(exception);

        // THEN
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("error"));
    }

    @Test
    @DisplayName("givenIllegalStateException_whenHandled_thenResponseBodyNotNull")
    void givenIllegalStateException_whenHandled_thenResponseBodyNotNull() {
        // GIVEN
        IllegalStateException exception = new IllegalStateException("Test state error");

        // WHEN
        ResponseEntity<Map<String, Object>> response = handler.handleIllegalState(exception);

        // THEN
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("error"));
    }
}
