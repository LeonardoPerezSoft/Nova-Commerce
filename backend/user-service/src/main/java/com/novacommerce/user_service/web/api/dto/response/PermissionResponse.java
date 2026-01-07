package com.novacommerce.user_service.web.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * DTO de respuesta para información de permiso.
 */
public record PermissionResponse(
    @JsonProperty("id")
    UUID id,

    @JsonProperty("name")
    String name,

    @JsonProperty("description")
    String description
) {}
