package com.novacommerce.user_service.adapter.in.web;

import com.novacommerce.user_service.application.port.in.ValidateUserCredentialsUseCase;
import com.novacommerce.user_service.web.api.dto.request.InternalUserValidationRequest;
import com.novacommerce.user_service.web.api.dto.response.InternalUserValidationResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador de entrada REST para endpoints internos de usuarios.
 * Implementa Clean Architecture delegando en los casos de uso.
 * 
 * IMPORTANTE: Este controlador debe estar protegido con API Key
 * para que solo auth-service pueda acceder.
 */
@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Internal User Management", description = "Endpoints internos para gestión de usuarios")
@Hidden
public class InternalUserRestController {

    private final ValidateUserCredentialsUseCase validateUserCredentialsUseCase;

    @PostMapping("/validate")
    @Operation(
        summary = "Validar credenciales de usuario",
        description = "Valida las credenciales de un usuario. Endpoint interno protegido con API Key, llamado por auth-service.",
        security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Credenciales válidas",
            content = @Content(schema = @Schema(implementation = InternalUserValidationResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas o API Key inválida")
    })
    public ResponseEntity<InternalUserValidationResponse> validateCredentials(
        @Valid @RequestBody InternalUserValidationRequest request) {
        
        log.info("Validación de credenciales solicitada para: {}", request.userIdentifier());
        
        InternalUserValidationResponse response = validateUserCredentialsUseCase.validateCredentials(
            request.userIdentifier(),
            request.password()
        );
        
        log.info("Credenciales validadas exitosamente para: {}", request.userIdentifier());
        
        return ResponseEntity.ok(response);
    }
}
