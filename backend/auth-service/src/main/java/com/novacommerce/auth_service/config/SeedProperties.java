package com.novacommerce.auth_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Propiedades de configuración para el seed de datos iniciales.
 * Se mapean desde el prefijo 'app.seed' en application.yml.
 */
@Configuration
@ConfigurationProperties(prefix = "app.seed")
@Getter
@Setter
public class SeedProperties {

    /**
     * Habilita o deshabilita el seed de datos.
     * Por defecto está deshabilitado para entornos de producción.
     */
    private boolean enabled = false;

    /**
     * Configuración del usuario administrador inicial.
     */
    private AdminConfig admin = new AdminConfig();

    /**
     * Configuración del usuario administrador.
     */
    @Getter
    @Setter
    public static class AdminConfig {
        private String username = "admin";
        private String email = "admin@nova.com";
        private String password = "admin123";
    }
}
