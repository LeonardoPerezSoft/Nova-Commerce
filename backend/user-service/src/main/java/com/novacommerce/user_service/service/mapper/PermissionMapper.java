package com.novacommerce.user_service.service.mapper;


import com.novacommerce.user_service.domain.model.Permission;
import com.novacommerce.user_service.web.api.dto.response.PermissionResponse;
import org.mapstruct.Mapper;

/**
 * Mapper para convertir entre la entidad Permission y sus DTOs.
 * Usa MapStruct para generar implementación automática.
 */
@Mapper(componentModel = "spring")
public interface PermissionMapper {

    /**
     * Convierte una entidad Permission a PermissionResponse.
     *
     * @param permission la entidad Permission
     * @return PermissionResponse
     */
    PermissionResponse permissionToPermissionResponse(Permission permission);
}
