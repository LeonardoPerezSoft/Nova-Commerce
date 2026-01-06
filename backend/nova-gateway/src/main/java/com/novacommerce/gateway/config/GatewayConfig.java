package com.novacommerce.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.novacommerce.gateway.routing.RouteConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuración de rutas del API Gateway
 * Define el enrutamiento de peticiones hacia los microservicios backend
 */
@Slf4j
@Configuration
public class GatewayConfig {

    @Value("${gateway.routes.auth-service.uri}")
    private String authServiceUri;

    @Value("${gateway.routes.auth-service.path}")
    private String authServicePath;

    @Value("${gateway.routes.user-service-users.uri}")
    private String userServiceUri;

    @Value("${gateway.routes.user-service-users.path}")
    private String userServiceUsersPath;

    @Value("${gateway.routes.user-service-roles.path}")
    private String userServiceRolesPath;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        log.info("Configuring Gateway routes");
        log.info("Route: {} -> {}", authServicePath, authServiceUri);
        log.info("Route: {} -> {}", userServiceUsersPath, userServiceUri);
        log.info("Route: {} -> {}", userServiceRolesPath, userServiceUri);

        return builder.routes()
                .route(RouteConstants.AUTH_SERVICE_ID, r -> r
                        .path(authServicePath)
                        .filters(f -> f
                                .stripPrefix(0)
                                .removeRequestHeader("Cookie"))
                        .uri(authServiceUri))
                .route("user-service-users", r -> r
                        .path(userServiceUsersPath)
                        .filters(f -> f
                                .stripPrefix(0)
                                .removeRequestHeader("Cookie"))
                        .uri(userServiceUri))
                .route("user-service-roles", r -> r
                        .path(userServiceRolesPath)
                        .filters(f -> f
                                .stripPrefix(0)
                                .removeRequestHeader("Cookie"))
                        .uri(userServiceUri))
                .build();
    }
}
