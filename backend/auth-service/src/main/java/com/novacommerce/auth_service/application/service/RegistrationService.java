package com.novacommerce.auth_service.application.service;

import com.novacommerce.auth_service.adapter.out.customer.CustomerServiceFeignClient;
import com.novacommerce.auth_service.adapter.out.customer.dto.CreateCustomerRequest;
import com.novacommerce.auth_service.adapter.out.customer.dto.CreateCustomerResponse;
import com.novacommerce.auth_service.adapter.out.user.UserServiceFeignClient;
import com.novacommerce.auth_service.adapter.out.user.dto.CreateUserRequest;
import com.novacommerce.auth_service.adapter.out.user.dto.CreateUserResponse;
import com.novacommerce.auth_service.application.port.in.RegisterClientUseCase;
import com.novacommerce.auth_service.web.api.dto.request.RegisterRequest;
import com.novacommerce.auth_service.web.api.dto.response.RegisterResponse;
import com.novacommerce.auth_service.web.rest.exceptions.DuplicateResourceException;
import com.novacommerce.auth_service.web.rest.exceptions.InvalidCredentialsException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de aplicación que implementa el caso de uso de registro público.
 * Orquesta la creación de usuario y cliente de forma sincrónica.
 * 
 * Responsabilidades:
 * - Validar datos del registro
 * - Crear usuario en user-service
 * - Crear cliente en customer-service
 * - Manejar errores y excepciones
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RegistrationService implements RegisterClientUseCase {

    private final UserServiceFeignClient userServiceClient;
    private final CustomerServiceFeignClient customerServiceClient;

    @Value("${app.client-role-id:80c97308-f6e5-42a4-9b7f-1df83b299f4f}")
    private String clientRoleId;

    @Value("${app.jwt.internal-api-key:nova-internal-service-key-2024}")
    private String internalApiKey;

    /**
     * Registra un nuevo cliente creando usuario y cliente de forma sincrónica.
     * 
     * Flujo:
     * 1. Crea Usuario en User-Service con rol CLIENT
     * 2. Crea Cliente en Customer-Service con nivel BRONZE
     * 3. Retorna información combinada
     * 
     * Si el usuario existe (409 Conflict), lanza DuplicateResourceException.
     * Si hay error de validación (400 Bad Request), lanza InvalidCredentialsException.
     * 
     * @param request datos de registro
     * @return respuesta con userId, customerId y datos
     */
    @Override
    public RegisterResponse register(RegisterRequest request) {
        log.info("Iniciando registro de nuevo cliente: {}", request.getEmail());
        
        try {
            // Paso 1: Crear usuario en user-service
            CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .roleIds(List.of(clientRoleId))
                .build();

            log.debug("Creando usuario: {}", request.getEmail());
            CreateUserResponse userResponse = userServiceClient.createUser(internalApiKey, userRequest);
            log.info("Usuario creado exitosamente: {} (ID: {})", request.getEmail(), userResponse.getId());

            // Paso 2: Crear cliente en customer-service
            CreateCustomerRequest customerRequest = CreateCustomerRequest.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                                .loyaltyLevel("BRONZE")
                .userId(userResponse.getId())
                .build();

            log.debug("Creando cliente: {}", request.getEmail());
            CreateCustomerResponse customerResponse = customerServiceClient.createCustomer(internalApiKey, customerRequest);
            log.info("Cliente creado exitosamente: {} (ID: {})", request.getEmail(), customerResponse.getId());

            // Paso 3: Construir respuesta
            RegisterResponse response = RegisterResponse.builder()
                .userId(userResponse.getId())
                .customerId(customerResponse.getId())
                .email(request.getEmail())
                .fullName(request.getFirstName() + " " + request.getLastName())
                .message("Registro exitoso. Ahora puedes iniciar sesión en /api/auth/login")
                .loginUrl("/api/auth/login")
                .build();

            log.info("Registro completado para: {} (User: {}, Customer: {})", 
                request.getEmail(), userResponse.getId(), customerResponse.getId());
            
            return response;

        } catch (FeignException.Conflict e) {
            log.warn("Email o username ya existe: {}", request.getEmail());
            throw new DuplicateResourceException("User", "email", request.getEmail());
            
        } catch (FeignException.BadRequest e) {
            log.error("Solicitud inválida al registrar cliente: {}", e.getMessage());
            throw new InvalidCredentialsException("Datos de registro inválidos");
            
        } catch (Exception e) {
            log.error("Error durante el registro de cliente: {}", e.getMessage(), e);
            throw new InvalidCredentialsException("Error al registrar nuevo cliente");
        }
    }
}
