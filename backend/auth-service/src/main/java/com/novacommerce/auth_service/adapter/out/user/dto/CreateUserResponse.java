package com.novacommerce.auth_service.adapter.out.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de creación de usuario desde user-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de creación de usuario")
public class CreateUserResponse {

    @Schema(description = "ID del usuario creado", example = "7eea2162-ff23-4d9e-b431-643e4dda2d0c")
    private String id;

    @Schema(description = "Username del usuario")
    private String username;

    @Schema(description = "Email del usuario")
    private String email;

    @Schema(description = "Estado del usuario (ACTIVE, INACTIVE, etc.)")
    private String status;
}
