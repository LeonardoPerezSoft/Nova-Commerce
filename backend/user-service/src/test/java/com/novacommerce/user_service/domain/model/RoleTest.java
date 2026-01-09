package com.novacommerce.user_service.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Role Domain Model Tests")
class RoleTest {

    private Role role;
    private Permission permission;

    @BeforeEach
    void setUp() {
        permission = Permission.builder()
                .id(UUID.randomUUID())
                .name("USER_READ")
                .description("Permission to read users")
                .build();

        role = Role.builder()
                .id(UUID.randomUUID())
                .name("ADMIN")
                .description("Administrator role")
                .permissions(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create role with all required fields")
    void testRoleCreation() {
        assertNotNull(role);
        assertNotNull(role.getId());
        assertEquals("ADMIN", role.getName());
        assertEquals("Administrator role", role.getDescription());
        assertNotNull(role.getPermissions());
        assertTrue(role.getPermissions().isEmpty());
    }

    @Test
    @DisplayName("Should allow setting role name")
    void testSetName() {
        role.setName("USER");
        assertEquals("USER", role.getName());
    }

    @Test
    @DisplayName("Should allow setting role description")
    void testSetDescription() {
        role.setDescription("Standard user role");
        assertEquals("Standard user role", role.getDescription());
    }

    @Test
    @DisplayName("Should add permission to role successfully")
    void testAddPermission() {
        role.addPermission(permission);
        
        assertEquals(1, role.getPermissions().size());
        assertTrue(role.getPermissions().contains(permission));
    }

    @Test
    @DisplayName("Should not add null permission")
    void testAddNullPermission() {
        role.addPermission(null);
        
        assertEquals(0, role.getPermissions().size());
    }

    @Test
    @DisplayName("Should remove permission from role successfully")
    void testRemovePermission() {
        role.addPermission(permission);
        assertEquals(1, role.getPermissions().size());
        
        role.removePermission(permission);
        assertEquals(0, role.getPermissions().size());
        assertFalse(role.getPermissions().contains(permission));
    }

    @Test
    @DisplayName("Should not fail when removing null permission")
    void testRemoveNullPermission() {
        role.addPermission(permission);
        assertEquals(1, role.getPermissions().size());
        
        role.removePermission(null);
        assertEquals(1, role.getPermissions().size());
    }

    @Test
    @DisplayName("Should use builder pattern correctly")
    void testBuilderPattern() {
        UUID testId = UUID.randomUUID();
        Role builtRole = Role.builder()
                .id(testId)
                .name("SALES")
                .description("Sales role")
                .build();

        assertEquals(testId, builtRole.getId());
        assertEquals("SALES", builtRole.getName());
        assertEquals("Sales role", builtRole.getDescription());
    }

    @Test
    @DisplayName("Should handle multiple permissions")
    void testMultiplePermissions() {
        Permission perm1 = Permission.builder().id(UUID.randomUUID()).name("USER_READ").build();
        Permission perm2 = Permission.builder().id(UUID.randomUUID()).name("USER_CREATE").build();
        Permission perm3 = Permission.builder().id(UUID.randomUUID()).name("USER_UPDATE").build();

        role.addPermission(perm1);
        role.addPermission(perm2);
        role.addPermission(perm3);

        assertEquals(3, role.getPermissions().size());
        assertTrue(role.getPermissions().contains(perm1));
        assertTrue(role.getPermissions().contains(perm2));
        assertTrue(role.getPermissions().contains(perm3));
    }

    @Test
    @DisplayName("Should allow null description")
    void testNullDescription() {
        role.setDescription(null);
        assertNull(role.getDescription());
    }

    @Test
    @DisplayName("Should set created at timestamp")
    void testSetCreatedAt() {
        LocalDateTime createdAt = LocalDateTime.now();
        role.setCreatedAt(createdAt);
        assertEquals(createdAt, role.getCreatedAt());
    }

    @Test
    @DisplayName("Should set updated at timestamp")
    void testSetUpdatedAt() {
        LocalDateTime updatedAt = LocalDateTime.now();
        role.setUpdatedAt(updatedAt);
        assertEquals(updatedAt, role.getUpdatedAt());
    }

    @Test
    @DisplayName("Should remove specific permission from multiple permissions")
    void testRemoveSpecificPermission() {
        Permission perm1 = Permission.builder().id(UUID.randomUUID()).name("USER_READ").build();
        Permission perm2 = Permission.builder().id(UUID.randomUUID()).name("USER_CREATE").build();

        role.addPermission(perm1);
        role.addPermission(perm2);
        assertEquals(2, role.getPermissions().size());

        role.removePermission(perm1);
        assertEquals(1, role.getPermissions().size());
        assertFalse(role.getPermissions().contains(perm1));
        assertTrue(role.getPermissions().contains(perm2));
    }
}
