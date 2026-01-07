package com.novacommerce.user_service.domain.enums;

/**
 * Enum que define los estados posibles de un usuario.
 */
public enum UserStatusEnum {
    ACTIVE("Usuario activo"),
    INACTIVE("Usuario inactivo"),
    LOCKED("Usuario bloqueado");

    private final String description;

    UserStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
