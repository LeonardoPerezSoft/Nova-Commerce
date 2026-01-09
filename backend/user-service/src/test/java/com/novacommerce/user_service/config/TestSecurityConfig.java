package com.novacommerce.user_service.config;

import com.novacommerce.user_service.adapter.in.filter.InternalApiKeyFilter;
import com.novacommerce.user_service.adapter.in.filter.JwtAuthenticationFilter;
import com.novacommerce.user_service.application.port.in.ValidateUserCredentialsUseCase;
import com.novacommerce.user_service.application.port.out.TokenValidatorPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

/**
 * Configuración de test para mockear componentes de seguridad.
 */
@TestConfiguration
public class TestSecurityConfig {

    /**
     * Mock del puerto validador de tokens para evitar validaciones reales en tests.
     */
    @Bean
    public TokenValidatorPort tokenValidatorPort() {
        return mock(TokenValidatorPort.class);
    }

    /**
     * Mock del caso de uso de validación de credenciales del usuario.
     */
    @Bean
    @Primary
    public ValidateUserCredentialsUseCase validateUserCredentialsUseCase() {
        return mock(ValidateUserCredentialsUseCase.class);
    }

    /**
     * Mock del filtro JWT para evitar validaciones reales en tests.
     */
    @Bean
    @Primary
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenValidatorPort());
    }

    /**
     * Mock del filtro de API Key interno con un valor de test.
     */
    @Bean
    @Primary
    public InternalApiKeyFilter internalApiKeyFilter() {
        return new InternalApiKeyFilter("test-api-key");
    }
}
