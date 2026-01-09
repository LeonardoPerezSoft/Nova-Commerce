package com.novacommerce.user_service.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Permission Domain Model Tests")
class PermissionTest {

    private Permission permission;

    @BeforeEach
    void setUp() {
        permission = Permission.builder()
                .id(UUID.randomUUID())
                .name("USER_READ")
                .description("Permission to read user data")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create permission with all required fields")
    void testPermissionCreation() {
        assertNotNull(permission);
        assertNotNull(permission.getId());
        assertEquals("USER_READ", permission.getName());
        assertEquals("Permission to read user data", permission.getDescription());
        assertNotNull(permission.getCreatedAt());
        assertNotNull(permission.getUpdatedAt());
    }

    @Test
    @DisplayName("Should allow setting permission name")
    void testSetName() {
        permission.setName("USER_WRITE");
        assertEquals("USER_WRITE", permission.getName());
    }

    @Test
    @DisplayName("Should allow setting permission description")
    void testSetDescription() {
        permission.setDescription("Permission to write user data");
        assertEquals("Permission to write user data", permission.getDescription());
    }

    @Test
    @DisplayName("Should use builder pattern correctly")
    void testBuilderPattern() {
        UUID testId = UUID.randomUUID();
        Permission builtPermission = Permission.builder()
                .id(testId)
                .name("ROLE_CREATE")
                .description("Permission to create roles")
                .build();

        assertEquals(testId, builtPermission.getId());
        assertEquals("ROLE_CREATE", builtPermission.getName());
        assertEquals("Permission to create roles", builtPermission.getDescription());
    }

    @Test
    @DisplayName("Should allow null description")
    void testNullDescription() {
        permission.setDescription(null);
        assertNull(permission.getDescription());
    }

    @Test
    @DisplayName("Should set created at timestamp")
    void testSetCreatedAt() {
        LocalDateTime createdAt = LocalDateTime.now();
        permission.setCreatedAt(createdAt);
        assertEquals(createdAt, permission.getCreatedAt());
    }

    @Test
    @DisplayName("Should set updated at timestamp")
    void testSetUpdatedAt() {
        LocalDateTime updatedAt = LocalDateTime.now();
        permission.setUpdatedAt(updatedAt);
        assertEquals(updatedAt, permission.getUpdatedAt());
    }

    @Test
    @DisplayName("Should create permission with UUID")
    void testPermissionWithUUID() {
        UUID customId = UUID.randomUUID();
        Permission customPermission = Permission.builder()
                .id(customId)
                .name("CUSTOM_PERMISSION")
                .build();

        assertEquals(customId, customPermission.getId());
    }

    @Test
    @DisplayName("Should handle different permission name formats")
    void testDifferentNameFormats() {
        permission.setName("USER_DELETE");
        assertEquals("USER_DELETE", permission.getName());

        permission.setName("AUTH_VALIDATE");
        assertEquals("AUTH_VALIDATE", permission.getName());

        permission.setName("ROLE_UPDATE");
        assertEquals("ROLE_UPDATE", permission.getName());
    }

    @Test
    @DisplayName("Should allow setting permission ID")
    void testSetId() {
        UUID newId = UUID.randomUUID();
        permission.setId(newId);
        assertEquals(newId, permission.getId());
    }

    @Test
    @DisplayName("Should handle long descriptions")
    void testLongDescription() {
        String longDesc = "This is a very long description that explains in detail what this permission allows users to do in the system";
        permission.setDescription(longDesc);
        assertEquals(longDesc, permission.getDescription());
    }
}
