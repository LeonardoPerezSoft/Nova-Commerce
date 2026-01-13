package com.novacommerce.user_service.application.port.in;

import com.novacommerce.user_service.web.api.dto.request.CreateUserRequest;
import com.novacommerce.user_service.web.api.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Puerto de entrada para gestión de usuarios.
 * Define los casos de uso para operaciones CRUD de usuarios.
 */
public interface ManageUsersUseCase {

    /**
     * Obtiene todos los usuarios con paginación.
     */
    Page<UserResponse> getAllUsers(Pageable pageable);

    /**
     * Obtiene un usuario por su UUID.
     */
    UserResponse getUserById(UUID id);

    /**
     * Crea un nuevo usuario.
     */
    UserResponse createUser(CreateUserRequest createUserRequest);

    /**
     * Actualiza un usuario existente.
     */
    UserResponse updateUser(UUID id, CreateUserRequest updateUserRequest);

    /**
     * Elimina un usuario.
     */
    void deleteUser(UUID id);
    
    /**
     * Actualiza el customerId de un usuario.
     */
    void updateCustomerId(UUID userId, Long customerId);

    /**
     * Verifica si un usuario es el usuario actual autenticado.
     */
    boolean isCurrentUser(UUID id);
}
