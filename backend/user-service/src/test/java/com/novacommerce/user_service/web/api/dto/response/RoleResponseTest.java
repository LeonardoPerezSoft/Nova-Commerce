package com.novacommerce.user_service.web.api.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RoleResponse DTO Tests")
class RoleResponseTest {

    private RoleResponse roleResponse;
    private UUID roleId;

    @BeforeEach
    void setUp() {
        roleId = UUID.randomUUID();

        PermissionResponse permissionResponse = new PermissionResponse(
                UUID.randomUUID(),
                "USER_READ",
                "Permission to read users"
        );

        Set<PermissionResponse> permissions = new HashSet<>();
        permissions.add(permissionResponse);

        roleResponse = new RoleResponse(
                roleId,
                "ADMIN",
                "Administrator role",
                permissions
        );
    }

    @Test
    @DisplayName("Should create RoleResponse with all fields")
    void testRoleResponseCreation() {
        assertNotNull(roleResponse);
        assertEquals(roleId, roleResponse.id());
        assertEquals("ADMIN", roleResponse.name());
        assertEquals("Administrator role", roleResponse.description());
        assertNotNull(roleResponse.permissions());
        assertEquals(1, roleResponse.permissions().size());
    }

    @Test
    @DisplayName("Should handle null description")
    void testNullDescription() {
        RoleResponse response = new RoleResponse(
                UUID.randomUUID(),
                "USER",
                null,
                new HashSet<>()
        );

        assertNull(response.description());
    }

    @Test
    @DisplayName("Should handle null permissions")
    void testNullPermissions() {
        RoleResponse response = new RoleResponse(
                UUID.randomUUID(),
                "USER",
                "User role",
                null
        );

        assertNull(response.permissions());
    }

    @Test
    @DisplayName("Should handle empty permissions")
    void testEmptyPermissions() {
        RoleResponse response = new RoleResponse(
                UUID.randomUUID(),
                "USER",
                "User role",
                new HashSet<>()
        );

        assertNotNull(response.permissions());
        assertTrue(response.permissions().isEmpty());
    }

    @Test
    @DisplayName("Should handle multiple permissions")
    void testMultiplePermissions() {
        Set<PermissionResponse> permissions = new HashSet<>();
        permissions.add(new PermissionResponse(UUID.randomUUID(), "USER_READ", "Read users"));
        permissions.add(new PermissionResponse(UUID.randomUUID(), "USER_CREATE", "Create users"));
        permissions.add(new PermissionResponse(UUID.randomUUID(), "USER_UPDATE", "Update users"));

        RoleResponse response = new RoleResponse(
                UUID.randomUUID(),
                "ADMIN",
                "Administrator role",
                permissions
        );

        assertEquals(3, response.permissions().size());
    }

    @Test
    @DisplayName("Should handle different role names")
    void testDifferentRoleNames() {
        RoleResponse admin = new RoleResponse(UUID.randomUUID(), "ADMIN", "Admin", new HashSet<>());
        assertEquals("ADMIN", admin.name());

        RoleResponse user = new RoleResponse(UUID.randomUUID(), "USER", "User", new HashSet<>());
        assertEquals("USER", user.name());

        RoleResponse sales = new RoleResponse(UUID.randomUUID(), "SALES", "Sales", new HashSet<>());
        assertEquals("SALES", sales.name());
    }

    @Test
    @DisplayName("Should preserve UUID format")
    void testUUIDFormat() {
        UUID testId = UUID.randomUUID();
        RoleResponse response = new RoleResponse(testId, "TEST", "Test role", new HashSet<>());

        assertEquals(testId, response.id());
        assertEquals(testId.toString(), response.id().toString());
    }

    @Test
    @DisplayName("Should handle long descriptions")
    void testLongDescription() {
        String longDesc = "This is a very long description that explains in detail what this role can do in the system";
        RoleResponse response = new RoleResponse(UUID.randomUUID(), "ADMIN", longDesc, new HashSet<>());

        assertEquals(longDesc, response.description());
    }

    @Test
    @DisplayName("Should create role with complete permission set")
    void testCompletePermissionSet() {
        Set<PermissionResponse> permissions = new HashSet<>();
        permissions.add(new PermissionResponse(UUID.randomUUID(), "USER_READ", "Read"));
        permissions.add(new PermissionResponse(UUID.randomUUID(), "USER_CREATE", "Create"));
        permissions.add(new PermissionResponse(UUID.randomUUID(), "USER_UPDATE", "Update"));
        permissions.add(new PermissionResponse(UUID.randomUUID(), "USER_DELETE", "Delete"));

        RoleResponse response = new RoleResponse(
                UUID.randomUUID(),
                "ADMIN",
                "Full admin access",
                permissions
        );

        assertEquals(4, response.permissions().size());
        assertTrue(response.permissions().stream()
                .anyMatch(p -> p.name().equals("USER_READ")));
        assertTrue(response.permissions().stream()
                .anyMatch(p -> p.name().equals("USER_CREATE")));
    }

    @Test
    @DisplayName("Should handle empty name")
    void testEmptyName() {
        RoleResponse response = new RoleResponse(UUID.randomUUID(), "", "Description", new HashSet<>());
        assertEquals("", response.name());
    }
}
