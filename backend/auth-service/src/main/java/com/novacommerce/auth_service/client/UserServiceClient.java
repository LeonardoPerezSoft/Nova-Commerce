package com.novacommerce.auth_service.client;


import com.novacommerce.auth_service.client.dto.UserValidationRequest;
import com.novacommerce.auth_service.client.dto.UserValidationResponse;
import com.novacommerce.auth_service.client.feign.UserServiceFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Feign Client para comunicación con user-service.
 * Se usa EXCLUSIVAMENTE durante el proceso de login para validar credenciales.
 * 
 * Comunicación:
 * - auth-service → user-service (UNIDIRECCIONAL)
 * - Endpoint interno (no expuesto públicamente)
 * - No requiere JWT (comunicación interna)
 */
@FeignClient(
    name = "user-service",
    url = "${user-service.url}",
    path = "/internal/users",
        configuration = UserServiceFeignConfig.class
)
public interface UserServiceClient {

    /**
     * Valida credenciales de usuario.
     * 
     * @param request contiene userIdentifier (username o email) y password
     * @return UserValidationResponse con información del usuario y sus roles/permisos
     */
    @PostMapping("/validate")
    UserValidationResponse validateCredentials(
        @RequestBody UserValidationRequest request,
        @RequestHeader("X-Internal-API-Key") String internalApiKey
    );
}
