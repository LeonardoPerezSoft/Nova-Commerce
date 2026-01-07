package com.novacommerce.user_service.service.mapper;


import com.novacommerce.user_service.domain.model.Role;
import com.novacommerce.user_service.web.api.dto.response.RoleResponse;
import org.mapstruct.Mapper;

/**
 * Mapper para convertir entre la entidad Role y sus DTOs.
 * Usa MapStruct para generar implementación automática.
 */
@Mapper(componentModel = "spring", uses = PermissionMapper.class)
public interface RoleMapper {

    /**
     * Convierte una entidad Role a RoleResponse.
     *
     * @param role la entidad Role
     * @return RoleResponse
     */
    RoleResponse roleToRoleResponse(Role role);
}
