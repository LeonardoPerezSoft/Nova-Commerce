package com.novacommerce.user_service.repository;


import com.novacommerce.user_service.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para la entidad User.
 * Proporciona métodos de acceso a datos para usuarios del sistema.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Busca un usuario por nombre de usuario.
     *
     * @param username el nombre de usuario a buscar
     * @return Optional conteniendo el usuario si existe
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca un usuario por nombre de usuario cargando roles y permisos.
     *
     * @param username el nombre de usuario a buscar
     * @return Optional conteniendo el usuario con sus roles y permisos si existe
     */
    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.roles r " +
           "LEFT JOIN FETCH r.permissions " +
           "WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);

    /**
     * Busca un usuario por email.
     *
     * @param email el email a buscar
     * @return Optional conteniendo el usuario si existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el nombre de usuario proporcionado.
     *
     * @param username el nombre de usuario a verificar
     * @return true si el usuario existe, false en caso contrario
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un usuario con el email proporcionado.
     *
     * @param email el email a verificar
     * @return true si el usuario existe, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Busca un usuario por username o email.
     * Importante para el endpoint de validación de auth-service.
     *
     * @param userIdentifier nombre de usuario o email
     * @return Optional con el usuario si existe
     */
    @Query("SELECT u FROM User u WHERE u.username = :userIdentifier OR u.email = :userIdentifier")
    Optional<User> findByUsernameOrEmail(@Param("userIdentifier") String userIdentifier);
}
