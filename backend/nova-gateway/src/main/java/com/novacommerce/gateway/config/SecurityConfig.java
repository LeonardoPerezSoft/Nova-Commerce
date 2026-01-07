package com.novacommerce.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuración de seguridad del gateway
 * Define beans relacionados con autenticación y autorización
 */
@Slf4j
@Configuration
public class SecurityConfig {

    @Bean
    public AntPathMatcher antPathMatcher() {
        log.debug("Creating AntPathMatcher bean");
        return new AntPathMatcher();
    }

    /**
     * Los puertos son inyectados automáticamente a través de sus implementaciones
     * (JwtTokenValidatorAdapter implementa TokenValidatorPort)
     */
}
