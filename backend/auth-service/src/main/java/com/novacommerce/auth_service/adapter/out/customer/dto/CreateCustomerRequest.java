package com.novacommerce.auth_service.adapter.out.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de creación de cliente en customer-service.
 * Comunicación interna, NO expuesta al frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud interna de creación de cliente")
public class CreateCustomerRequest {

    @Schema(description = "Email del cliente", example = "leonardo@sofka.com.co")
    private String email;

    @Schema(description = "Nombre del cliente", example = "Leonardo")
    private String firstName;

    @Schema(description = "Apellido del cliente", example = "Pérez")
    private String lastName;

    @Schema(description = "Teléfono de contacto", example = "3114483021")
    private String phone;
    @Schema(description = "Nivel de lealtad inicial", example = "BRONZE")
    private String loyaltyLevel;


    @Schema(description = "ID del usuario asociado")
    private String userId;
}
