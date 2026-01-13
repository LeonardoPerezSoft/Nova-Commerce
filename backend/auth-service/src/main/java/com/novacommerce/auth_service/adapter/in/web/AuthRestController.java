package com.novacommerce.auth_service.adapter.in.web;

import com.novacommerce.auth_service.application.port.in.AuthenticateUserUseCase;
import com.novacommerce.auth_service.application.port.in.RefreshTokenUseCase;
import com.novacommerce.auth_service.application.port.in.RegisterClientUseCase;
import com.novacommerce.auth_service.application.port.in.ValidateTokenUseCase;
import com.novacommerce.auth_service.web.api.dto.request.LoginRequest;
import com.novacommerce.auth_service.web.api.dto.request.RefreshTokenRequest;
import com.novacommerce.auth_service.web.api.dto.request.RegisterRequest;
import com.novacommerce.auth_service.web.api.dto.response.LoginResponse;
import com.novacommerce.auth_service.web.api.dto.response.RegisterResponse;
import com.novacommerce.auth_service.web.api.dto.response.TokenValidationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Adaptador de entrada (Controller REST) para autenticación
 * Expone los endpoints HTTP y delega a los casos de uso
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints de autenticación y gestión de tokens JWT")
public class AuthRestController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final ValidateTokenUseCase validateTokenUseCase;
    private final RegisterClientUseCase registerClientUseCase;

    @PostMapping("/login")
    @Operation(
        summary = "Login de usuario",
        description = "Autentica un usuario con username o email y contraseña. Retorna tokens JWT.",
        security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso", 
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.userIdentifier());
        LoginResponse response = authenticateUserUseCase.authenticate(loginRequest);
        log.info("Login successful for user: {}", loginRequest.userIdentifier());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/public/register")
    @Operation(
        summary = "Registrar nuevo cliente",
        description = "Endpoint público de registro. Crea un nuevo usuario y cliente sin requeri autenticación.",
        security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cliente registrado exitosamente",
            content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos de registro inválidos o incompletos"),
        @ApiResponse(responseCode = "409", description = "Email o username ya existe")
    })
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Registro público: {}", registerRequest.getEmail());
        RegisterResponse response = registerClientUseCase.register(registerRequest);
        log.info("Registro exitoso: {}", registerRequest.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/refresh")
    @Operation(
        summary = "Refrescar token de acceso",
        description = "Genera un nuevo access token usando un refresh token válido.",
        security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refrescado exitosamente",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "Refresh token inválido o expirado")
    })
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("Token refresh attempt");
        LoginResponse response = refreshTokenUseCase.refreshToken(refreshTokenRequest);
        log.info("Token refreshed successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    @Operation(
        summary = "Validar token JWT",
        security = @SecurityRequirement(name = "bearerAuth"),
        description = "Valida un token JWT y retorna su estado junto con la información del usuario."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Validación completada",
            content = @Content(schema = @Schema(implementation = TokenValidationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Token no proporcionado")
    })
    public ResponseEntity<TokenValidationResponse> validateToken(
        @RequestHeader(name = "Authorization", required = false) String authorizationHeader) {
        log.debug("Token validation requested");
        
        // Extraer token del header "Bearer <token>"
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }
        
        TokenValidationResponse response = validateTokenUseCase.validateToken(token);
        return ResponseEntity.ok(response);
    }
}
