package com.novacommerce.user_service.web.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;
import java.util.UUID;

/**
 * DTO de respuesta para información de rol.
 * Incluye el rol y sus permisos asociados.
 */
public record RoleResponse(
    @JsonProperty("id")
    UUID id,

    @JsonProperty("name")
    String name,

    @JsonProperty("description")
    String description,

    @JsonProperty("permissions")
    Set<PermissionResponse> permissions
) {}
