package com.novacommerce.user_service.application.port.out;

import com.novacommerce.user_service.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para persistencia de usuarios.
 * Define las operaciones de repositorio necesarias para usuarios.
 */
public interface UserPersistencePort {

    /**
     * Encuentra todos los usuarios con paginación.
     */
    Page<User> findAll(Pageable pageable);

    /**
     * Encuentra un usuario por su ID.
     */
    Optional<User> findById(UUID id);

    /**
     * Encuentra un usuario por su username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Encuentra un usuario por su email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Encuentra un usuario por username o email.
     */
    Optional<User> findByUsernameOrEmail(String identifier);

    /**
     * Verifica si existe un usuario con el username dado.
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un usuario con el email dado.
     */
    boolean existsByEmail(String email);

    /**
     * Guarda un usuario.
     */
    User save(User user);

    /**
     * Elimina un usuario por su ID.
     */
    void deleteById(UUID id);
}
