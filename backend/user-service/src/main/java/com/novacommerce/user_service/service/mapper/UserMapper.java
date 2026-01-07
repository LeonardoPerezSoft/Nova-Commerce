package com.novacommerce.user_service.service.mapper;


import com.novacommerce.user_service.domain.model.User;
import com.novacommerce.user_service.web.api.dto.response.UserResponse;
import org.mapstruct.Mapper;

/**
 * Mapper para convertir entre la entidad User y sus DTOs.
 * Usa MapStruct para generar implementación automática.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Convierte una entidad User a UserResponse.
     *
     * @param user la entidad User
     * @return UserResponse
     */
    UserResponse userToUserResponse(User user);
}
