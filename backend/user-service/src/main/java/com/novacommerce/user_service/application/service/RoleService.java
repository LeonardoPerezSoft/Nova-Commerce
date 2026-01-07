package com.novacommerce.user_service.application.service;

import com.novacommerce.user_service.application.port.in.ManageRolesUseCase;
import com.novacommerce.user_service.application.port.out.RolePersistencePort;
import com.novacommerce.user_service.domain.model.Permission;
import com.novacommerce.user_service.domain.model.Role;
import com.novacommerce.user_service.repository.PermissionRepository;
import com.novacommerce.user_service.service.mapper.RoleMapper;
import com.novacommerce.user_service.web.api.dto.request.CreateRoleRequest;
import com.novacommerce.user_service.web.api.dto.response.RoleResponse;
import com.novacommerce.user_service.web.rest.exceptions.DuplicateResourceException;
import com.novacommerce.user_service.web.rest.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación que implementa los casos de uso de gestión de roles.
 * Orquesta las operaciones usando los puertos definidos (Clean Architecture).
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RoleService implements ManageRolesUseCase {

    private final RolePersistencePort rolePersistencePort;
    private final PermissionRepository permissionRepository; // TODO: Crear PermissionPersistencePort
    private final RoleMapper roleMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        log.info("Obteniendo todos los roles");
        return rolePersistencePort.findAll().stream()
            .map(roleMapper::roleToRoleResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(UUID id) {
        log.info("Obteniendo rol: {}", id);
        return rolePersistencePort.findById(id)
            .map(roleMapper::roleToRoleResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
    }

    @Override
    public RoleResponse createRole(CreateRoleRequest createRoleRequest) {
        log.info("Creando rol: {}", createRoleRequest.name());

        if (rolePersistencePort.existsByName(createRoleRequest.name())) {
            throw new DuplicateResourceException("Role", "name", createRoleRequest.name());
        }

        Set<Permission> permissions = loadPermissions(createRoleRequest.permissionIds());

        Role role = Role.builder()
            .name(createRoleRequest.name())
            .description(createRoleRequest.description())
            .permissions(permissions)
            .build();

        Role savedRole = rolePersistencePort.save(role);
        log.info("Rol creado exitosamente: {}", savedRole.getId());

        return roleMapper.roleToRoleResponse(savedRole);
    }

    @Override
    public void deleteRole(UUID id) {
        log.info("Eliminando rol: {}", id);

        if (!rolePersistencePort.findById(id).isPresent()) {
            throw new ResourceNotFoundException("Role", "id", id);
        }

        rolePersistencePort.deleteById(id);
        log.info("Rol eliminado exitosamente: {}", id);
    }

    private Set<Permission> loadPermissions(Set<UUID> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return new HashSet<>();
        }

        Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));

        if (permissions.size() != permissionIds.size()) {
            Set<UUID> foundIds = permissions.stream()
                .map(Permission::getId)
                .collect(Collectors.toSet());
            Set<UUID> notFoundIds = permissionIds.stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toSet());

            log.warn("Permisos no encontrados: {}", notFoundIds);
        }

        return permissions;
    }
}
