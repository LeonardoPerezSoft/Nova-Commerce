package com.novacommerce.user_service.service.mapper;

import com.novacommerce.user_service.domain.model.Permission;
import com.novacommerce.user_service.domain.model.Role;
import com.novacommerce.user_service.web.api.dto.response.RoleResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("RoleMapper Tests")
class RoleMapperTest {

    @Autowired
    private RoleMapper roleMapper;

    private Role role;
    private Permission permission;

    @BeforeEach
    void setUp() {
        permission = Permission.builder()
                .id(UUID.randomUUID())
                .name("USER_READ")
                .description("Permission to read users")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Set<Permission> permissions = new HashSet<>();
        permissions.add(permission);

        role = Role.builder()
                .id(UUID.randomUUID())
                .name("ADMIN")
                .description("Administrator role")
                .permissions(permissions)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should map Role to RoleResponse")
    void testRoleToRoleResponse() {
        RoleResponse response = roleMapper.roleToRoleResponse(role);

        assertNotNull(response);
        assertEquals(role.getId(), response.id());
        assertEquals(role.getName(), response.name());
        assertEquals(role.getDescription(), response.description());
    }

    @Test
    @DisplayName("Should map permissions correctly")
    void testPermissionsMapping() {
        RoleResponse response = roleMapper.roleToRoleResponse(role);

        assertNotNull(response.permissions());
        assertEquals(1, response.permissions().size());
        assertTrue(response.permissions().stream()
                .anyMatch(p -> p.name().equals("USER_READ")));
    }

    @Test
    @DisplayName("Should handle null role")
    void testNullRole() {
        RoleResponse response = roleMapper.roleToRoleResponse(null);
        assertNull(response);
    }

    @Test
    @DisplayName("Should handle role without permissions")
    void testRoleWithoutPermissions() {
        Role roleWithoutPermissions = Role.builder()
                .id(UUID.randomUUID())
                .name("USER")
                .description("User role")
                .permissions(new HashSet<>())
                .build();

        RoleResponse response = roleMapper.roleToRoleResponse(roleWithoutPermissions);

        assertNotNull(response);
        assertNotNull(response.permissions());
        assertTrue(response.permissions().isEmpty());
    }

    @Test
    @DisplayName("Should handle null description")
    void testNullDescription() {
        role.setDescription(null);
        RoleResponse response = roleMapper.roleToRoleResponse(role);

        assertNotNull(response);
        assertNull(response.description());
    }

    @Test
    @DisplayName("Should handle multiple permissions")
    void testMultiplePermissions() {
        Permission createPermission = Permission.builder()
                .id(UUID.randomUUID())
                .name("USER_CREATE")
                .description("Create users")
                .build();

        Permission updatePermission = Permission.builder()
                .id(UUID.randomUUID())
                .name("USER_UPDATE")
                .description("Update users")
                .build();

        role.addPermission(createPermission);
        role.addPermission(updatePermission);

        RoleResponse response = roleMapper.roleToRoleResponse(role);

        assertEquals(3, response.permissions().size());
        assertTrue(response.permissions().stream()
                .anyMatch(p -> p.name().equals("USER_READ")));
        assertTrue(response.permissions().stream()
                .anyMatch(p -> p.name().equals("USER_CREATE")));
        assertTrue(response.permissions().stream()
                .anyMatch(p -> p.name().equals("USER_UPDATE")));
    }

    @Test
    @DisplayName("Should map UUID correctly")
    void testUUIDMapping() {
        UUID roleId = UUID.randomUUID();
        role.setId(roleId);

        RoleResponse response = roleMapper.roleToRoleResponse(role);

        assertEquals(roleId, response.id());
    }

    @Test
    @DisplayName("Should map different role names")
    void testDifferentRoleNames() {
        role.setName("SALES");
        RoleResponse salesResponse = roleMapper.roleToRoleResponse(role);
        assertEquals("SALES", salesResponse.name());

        role.setName("USER");
        RoleResponse userResponse = roleMapper.roleToRoleResponse(role);
        assertEquals("USER", userResponse.name());
    }

    @Test
    @DisplayName("Should map permission details completely")
    void testCompletePermissionMapping() {
        RoleResponse response = roleMapper.roleToRoleResponse(role);

        response.permissions().forEach(permResponse -> {
            assertNotNull(permResponse.id());
            assertNotNull(permResponse.name());
            assertNotNull(permResponse.description());
        });
    }

    @Test
    @DisplayName("Should handle empty permission set")
    void testEmptyPermissionSet() {
        role.setPermissions(new HashSet<>());
        RoleResponse response = roleMapper.roleToRoleResponse(role);

        assertNotNull(response.permissions());
        assertEquals(0, response.permissions().size());
    }

    @Test
    @DisplayName("Should preserve role name and description")
    void testNameAndDescriptionPreservation() {
        role.setName("CUSTOM_ROLE");
        role.setDescription("Custom role description");

        RoleResponse response = roleMapper.roleToRoleResponse(role);

        assertEquals("CUSTOM_ROLE", response.name());
        assertEquals("Custom role description", response.description());
    }
}
