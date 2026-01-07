package com.novacommerce.user_service.repository;


import com.novacommerce.user_service.domain.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para la entidad Permission.
 * Proporciona métodos de acceso a datos para permisos del sistema.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    /**
     * Busca un permiso por nombre.
     *
     * @param name el nombre del permiso a buscar
     * @return Optional conteniendo el permiso si existe
     */
    Optional<Permission> findByName(String name);
}
