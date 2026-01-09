package com.novacommerce.user_service.web.api.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PermissionResponse DTO Tests")
class PermissionResponseTest {

    private PermissionResponse permissionResponse;
    private UUID permissionId;

    @BeforeEach
    void setUp() {
        permissionId = UUID.randomUUID();
        permissionResponse = new PermissionResponse(
                permissionId,
                "USER_READ",
                "Permission to read user data"
        );
    }

    @Test
    @DisplayName("Should create PermissionResponse with all fields")
    void testPermissionResponseCreation() {
        assertNotNull(permissionResponse);
        assertEquals(permissionId, permissionResponse.id());
        assertEquals("USER_READ", permissionResponse.name());
        assertEquals("Permission to read user data", permissionResponse.description());
    }

    @Test
    @DisplayName("Should handle null description")
    void testNullDescription() {
        PermissionResponse response = new PermissionResponse(
                UUID.randomUUID(),
                "USER_WRITE",
                null
        );

        assertNull(response.description());
    }

    @Test
    @DisplayName("Should handle different permission names")
    void testDifferentPermissionNames() {
        PermissionResponse userRead = new PermissionResponse(UUID.randomUUID(), "USER_READ", "Read");
        assertEquals("USER_READ", userRead.name());

        PermissionResponse userCreate = new PermissionResponse(UUID.randomUUID(), "USER_CREATE", "Create");
        assertEquals("USER_CREATE", userCreate.name());

        PermissionResponse roleUpdate = new PermissionResponse(UUID.randomUUID(), "ROLE_UPDATE", "Update");
        assertEquals("ROLE_UPDATE", roleUpdate.name());

        PermissionResponse authValidate = new PermissionResponse(UUID.randomUUID(), "AUTH_VALIDATE", "Validate");
        assertEquals("AUTH_VALIDATE", authValidate.name());
    }

    @Test
    @DisplayName("Should preserve UUID format")
    void testUUIDFormat() {
        UUID testId = UUID.randomUUID();
        PermissionResponse response = new PermissionResponse(testId, "TEST_PERMISSION", "Test");

        assertEquals(testId, response.id());
        assertEquals(testId.toString(), response.id().toString());
    }

    @Test
    @DisplayName("Should handle long descriptions")
    void testLongDescription() {
        String longDesc = "This permission allows users to perform a very specific action in the system with detailed constraints";
        PermissionResponse response = new PermissionResponse(UUID.randomUUID(), "COMPLEX_ACTION", longDesc);

        assertEquals(longDesc, response.description());
    }

    @Test
    @DisplayName("Should handle empty description")
    void testEmptyDescription() {
        PermissionResponse response = new PermissionResponse(UUID.randomUUID(), "PERMISSION", "");
        assertEquals("", response.description());
    }

    @Test
    @DisplayName("Should create permission with standard naming convention")
    void testStandardNamingConvention() {
        PermissionResponse response = new PermissionResponse(
                UUID.randomUUID(),
                "ENTITY_ACTION",
                "Description"
        );

        assertTrue(response.name().contains("_"));
        assertEquals("ENTITY_ACTION", response.name());
    }

    @Test
    @DisplayName("Should handle permission for user operations")
    void testUserPermissions() {
        PermissionResponse userRead = new PermissionResponse(UUID.randomUUID(), "USER_READ", "Read users");
        PermissionResponse userCreate = new PermissionResponse(UUID.randomUUID(), "USER_CREATE", "Create users");
        PermissionResponse userUpdate = new PermissionResponse(UUID.randomUUID(), "USER_UPDATE", "Update users");
        PermissionResponse userDelete = new PermissionResponse(UUID.randomUUID(), "USER_DELETE", "Delete users");

        assertEquals("USER_READ", userRead.name());
        assertEquals("USER_CREATE", userCreate.name());
        assertEquals("USER_UPDATE", userUpdate.name());
        assertEquals("USER_DELETE", userDelete.name());
    }

    @Test
    @DisplayName("Should handle permission for role operations")
    void testRolePermissions() {
        PermissionResponse roleRead = new PermissionResponse(UUID.randomUUID(), "ROLE_READ", "Read roles");
        PermissionResponse roleCreate = new PermissionResponse(UUID.randomUUID(), "ROLE_CREATE", "Create roles");
        PermissionResponse roleUpdate = new PermissionResponse(UUID.randomUUID(), "ROLE_UPDATE", "Update roles");
        PermissionResponse roleDelete = new PermissionResponse(UUID.randomUUID(), "ROLE_DELETE", "Delete roles");

        assertEquals("ROLE_READ", roleRead.name());
        assertEquals("ROLE_CREATE", roleCreate.name());
        assertEquals("ROLE_UPDATE", roleUpdate.name());
        assertEquals("ROLE_DELETE", roleDelete.name());
    }

    @Test
    @DisplayName("Should handle auth permissions")
    void testAuthPermissions() {
        PermissionResponse authValidate = new PermissionResponse(
                UUID.randomUUID(),
                "AUTH_VALIDATE",
                "Validate JWT tokens"
        );

        assertEquals("AUTH_VALIDATE", authValidate.name());
        assertEquals("Validate JWT tokens", authValidate.description());
    }
}
