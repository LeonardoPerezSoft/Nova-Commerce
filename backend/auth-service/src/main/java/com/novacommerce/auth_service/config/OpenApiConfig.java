package com.novacommerce.auth_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para el auth-service.
 * Define la documentación automática de la API REST.
 */
@Configuration
public class OpenApiConfig {

    @Value("${app.name:Auth Service}")
    private String appName;

    @Value("${app.description:Servicio de autenticación y autorización centralizada para Nexatec Platform}")
    private String appDescription;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${app.contact.name:Automatec Team}")
    private String contactName;

    @Value("${app.contact.email:support@automatec.com}")
    private String contactEmail;

    @Value("${app.contact.url:https://automatec.com}")
    private String contactUrl;

    @Value("${server.port:8081}")
    private String serverPort;

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    /**
     * Configura la definición de OpenAPI.
     *
     * @return configuración de OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(apiInfo())
            .servers(apiServers())
            .components(apiComponents())
            .addSecurityItem(securityRequirement());
    }

    /**
     * Información general del API.
     *
     * @return información del API
     */
    private Info apiInfo() {
        return new Info()
            .title(appName)
            .description(appDescription)
            .version(appVersion)
            .contact(apiContact())
            .license(apiLicense());
    }

    /**
     * Información de contacto.
     *
     * @return contacto del equipo
     */
    private Contact apiContact() {
        return new Contact()
            .name(contactName)
            .email(contactEmail)
            .url(contactUrl);
    }

    /**
     * Información de licencia.
     *
     * @return licencia del API
     */
    private License apiLicense() {
        return new License()
            .name("Proprietary")
            .url(contactUrl);
    }

    /**
     * Servidores disponibles.
     *
     * @return lista de servidores
     */
    private List<Server> apiServers() {
        Server localServer = new Server()
            .url("http://localhost:" + serverPort)
            .description("Local Development Server");

        Server devServer = new Server()
            .url("https://api-dev.nexatec.com/auth")
            .description("Development Server");

        Server prodServer = new Server()
            .url("https://api.nexatec.com/auth")
            .description("Production Server");

        return List.of(localServer, devServer, prodServer);
    }

    /**
     * Componentes de seguridad y esquemas.
     *
     * @return componentes de OpenAPI
     */
    private Components apiComponents() {
        return new Components()
            .addSecuritySchemes(SECURITY_SCHEME_NAME, securityScheme());
    }

    /**
     * Esquema de seguridad JWT Bearer.
     *
     * @return esquema de seguridad
     */
    private SecurityScheme securityScheme() {
        return new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("JWT token de autenticación. Formato: Bearer <token>");
    }

    /**
     * Requerimiento de seguridad global.
     * Aplica a todos los endpoints excepto los públicos.
     *
     * @return requerimiento de seguridad
     */
    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList(SECURITY_SCHEME_NAME);
    }
}
