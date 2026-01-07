package com.novacommerce.auth_service.client.feign;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign configuration to automatically send the internal API key header
 * when calling user-service internal endpoints.
 */
@Configuration
public class UserServiceFeignConfig {

    @Value("${app.jwt.internal-api-key}")
    private String internalApiKey;

    @Bean
    public RequestInterceptor internalApiKeyInterceptor() {
        return template -> {
            // No imprimir el valor; solo confirmar que se añade
            template.header("X-Internal-API-Key", internalApiKey);
        };
    }
}
