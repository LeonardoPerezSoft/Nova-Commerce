package com.novacommerce.auth_service.adapter.out.user;

import com.novacommerce.auth_service.adapter.out.user.dto.CreateUserRequest;
import com.novacommerce.auth_service.adapter.out.user.dto.CreateUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Feign Client para comunicación con user-service.
 * Se usa para crear usuarios durante el registro público.
 */
@FeignClient(
    name = "user-service-registration",
    url = "${app.services.user-service.url}"
)
public interface UserServiceFeignClient {

    /**
     * Crea un nuevo usuario en user-service mediante endpoint interno.
     * 
     * @param apiKey API Key para autenticación interna
     * @param request datos del usuario a crear
     * @return respuesta con ID del usuario creado
     */
    @PostMapping("/internal/users")
    CreateUserResponse createUser(
        @RequestHeader("X-Internal-API-Key") String apiKey,
        @RequestBody CreateUserRequest request
    );
}
