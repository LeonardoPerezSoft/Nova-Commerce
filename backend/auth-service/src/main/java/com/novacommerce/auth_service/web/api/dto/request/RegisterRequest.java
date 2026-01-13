package com.novacommerce.auth_service.web.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de registro público de nuevo cliente.
 * No requiere autenticación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud de registro público de nuevo cliente")
public class RegisterRequest {

    @NotBlank(message = "Username es requerido")
    @Size(min = 3, max = 50, message = "Username debe tener entre 3 y 50 caracteres")
    @Schema(description = "Username único del usuario", example = "leonardo.perez")
    private String username;

    @NotBlank(message = "Email es requerido")
    @Email(message = "Email debe ser válido")
    @Schema(description = "Email único del usuario", example = "leonardo@sofka.com.co")
    private String email;

    @NotBlank(message = "Contraseña es requerida")
    @Size(min = 8, message = "Contraseña debe tener mínimo 8 caracteres")
    @Schema(description = "Contraseña (se cifrará con BCrypt)", example = "Leonardo12345")
    private String password;

    @NotBlank(message = "Nombre es requerido")
    @Schema(description = "Nombre del cliente", example = "Leonardo")
    private String firstName;

    @NotBlank(message = "Apellido es requerido")
    @Schema(description = "Apellido del cliente", example = "Pérez")
    private String lastName;

    @Pattern(regexp = "^[0-9+()\\-\\s]{10,}$", message = "Teléfono debe ser válido")
    @Schema(description = "Teléfono de contacto", example = "3114483021")
    private String phone;
}
