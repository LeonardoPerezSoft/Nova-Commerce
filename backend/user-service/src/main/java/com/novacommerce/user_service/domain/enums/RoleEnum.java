package com.novacommerce.user_service.domain.enums;

/**
 * Enum que define los roles disponibles en el sistema.
 * Cada rol representa un conjunto de permisos específicos.
 */
public enum RoleEnum {
    ADMIN("Administrador del sistema"),
    SALES("Ventas"),
    USER("Usuario estándar");

    private final String description;

    RoleEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
