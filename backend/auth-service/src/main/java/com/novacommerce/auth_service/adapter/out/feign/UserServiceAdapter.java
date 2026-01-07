package com.novacommerce.auth_service.adapter.out.feign;

import com.novacommerce.auth_service.application.port.out.UserValidationPort;
import com.novacommerce.auth_service.client.UserServiceClient;
import com.novacommerce.auth_service.client.dto.UserValidationRequest;
import com.novacommerce.auth_service.client.dto.UserValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida para validación de usuarios usando Feign Client
 * Implementa UserValidationPort delegando a user-service
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UserServiceAdapter implements UserValidationPort {

    private final UserServiceClient userServiceClient;

    @Override
    public UserValidationResponse validateCredentials(UserValidationRequest request) {
        log.debug("Validating user credentials via Feign Client for: {}", request.userIdentifier());
        
        try {
            UserValidationResponse response = userServiceClient.validateCredentials(request);
            log.debug("User validation successful for: {}", response.username());
            return response;
        } catch (Exception e) {
            log.error("Error validating user credentials: {}", e.getMessage(), e);
            throw e;
        }
    }
}
