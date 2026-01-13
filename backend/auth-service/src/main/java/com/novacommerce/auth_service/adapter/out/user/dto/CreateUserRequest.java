package com.novacommerce.auth_service.adapter.out.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para solicitud de creación de usuario en user-service.
 * Comunicación interna, NO expuesta al frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud interna de creación de usuario")
public class CreateUserRequest {

    @Schema(description = "Username único", example = "leonardo.perez")
    private String username;

    @Schema(description = "Email único", example = "leonardo@sofka.com.co")
    private String email;

    @Schema(description = "Contraseña en texto plano (será cifrada en user-service)")
    private String password;

    @Schema(description = "IDs de roles a asignar")
    private List<String> roleIds;
}
