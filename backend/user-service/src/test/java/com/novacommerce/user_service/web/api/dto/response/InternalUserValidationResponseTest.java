package com.novacommerce.user_service.web.api.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InternalUserValidationResponse DTO Tests")
class InternalUserValidationResponseTest {

    private InternalUserValidationResponse response;

    @BeforeEach
    void setUp() {
        Set<String> roles = new HashSet<>();
        roles.add("ADMIN");
        roles.add("USER");

        Set<String> permissions = new HashSet<>();
        permissions.add("USER_READ");
        permissions.add("USER_CREATE");

        response = InternalUserValidationResponse.builder()
                .username("testuser")
                .email("test@example.com")
                .enabled(true)
                .locked(false)
                .roles(roles)
                .permissions(permissions)
                .build();
    }

    @Test
    @DisplayName("Should create InternalUserValidationResponse with all fields")
    void testResponseCreation() {
        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertTrue(response.getEnabled());
        assertFalse(response.getLocked());
        assertEquals(2, response.getRoles().size());
        assertEquals(2, response.getPermissions().size());
    }

    @Test
    @DisplayName("Should use builder pattern correctly")
    void testBuilderPattern() {
        Set<String> roles = Set.of("ADMIN");
        Set<String> permissions = Set.of("USER_READ", "USER_CREATE", "USER_UPDATE");

        InternalUserValidationResponse built = InternalUserValidationResponse.builder()
                .username("admin")
                .email("admin@example.com")
                .enabled(true)
                .locked(false)
                .roles(roles)
                .permissions(permissions)
                .build();

        assertEquals("admin", built.getUsername());
        assertEquals("admin@example.com", built.getEmail());
        assertEquals(1, built.getRoles().size());
        assertEquals(3, built.getPermissions().size());
    }

    @Test
    @DisplayName("Should handle null roles")
    void testNullRoles() {
        InternalUserValidationResponse validationResponse = InternalUserValidationResponse.builder()
                .username("testuser")
                .email("test@example.com")
                .enabled(true)
                .locked(false)
                .roles(null)
                .permissions(new HashSet<>())
                .build();

        assertNull(validationResponse.getRoles());
    }

    @Test
    @DisplayName("Should handle null permissions")
    void testNullPermissions() {
        InternalUserValidationResponse validationResponse = InternalUserValidationResponse.builder()
                .username("testuser")
                .email("test@example.com")
                .enabled(true)
                .locked(false)
                .roles(new HashSet<>())
                .permissions(null)
                .build();

        assertNull(validationResponse.getPermissions());
    }

    @Test
    @DisplayName("Should handle empty roles and permissions")
    void testEmptyRolesAndPermissions() {
        InternalUserValidationResponse validationResponse = InternalUserValidationResponse.builder()
                .username("testuser")
                .email("test@example.com")
                .enabled(true)
                .locked(false)
                .roles(new HashSet<>())
                .permissions(new HashSet<>())
                .build();

        assertNotNull(validationResponse.getRoles());
        assertNotNull(validationResponse.getPermissions());
        assertTrue(validationResponse.getRoles().isEmpty());
        assertTrue(validationResponse.getPermissions().isEmpty());
    }

    @Test
    @DisplayName("Should handle disabled user")
    void testDisabledUser() {
        InternalUserValidationResponse disabledUser = InternalUserValidationResponse.builder()
                .username("disableduser")
                .email("disabled@example.com")
                .enabled(false)
                .locked(false)
                .roles(new HashSet<>())
                .permissions(new HashSet<>())
                .build();

        assertFalse(disabledUser.getEnabled());
    }

    @Test
    @DisplayName("Should handle locked user")
    void testLockedUser() {
        InternalUserValidationResponse lockedUser = InternalUserValidationResponse.builder()
                .username("lockeduser")
                .email("locked@example.com")
                .enabled(true)
                .locked(true)
                .roles(new HashSet<>())
                .permissions(new HashSet<>())
                .build();

        assertTrue(lockedUser.getLocked());
    }

    @Test
    @DisplayName("Should handle multiple roles")
    void testMultipleRoles() {
        Set<String> roles = new HashSet<>();
        roles.add("ADMIN");
        roles.add("USER");
        roles.add("SALES");

        InternalUserValidationResponse validationResponse = InternalUserValidationResponse.builder()
                .username("multiuser")
                .email("multi@example.com")
                .enabled(true)
                .locked(false)
                .roles(roles)
                .permissions(new HashSet<>())
                .build();

        assertEquals(3, validationResponse.getRoles().size());
        assertTrue(validationResponse.getRoles().contains("ADMIN"));
        assertTrue(validationResponse.getRoles().contains("USER"));
        assertTrue(validationResponse.getRoles().contains("SALES"));
    }

    @Test
    @DisplayName("Should handle multiple permissions")
    void testMultiplePermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.add("USER_READ");
        permissions.add("USER_CREATE");
        permissions.add("USER_UPDATE");
        permissions.add("USER_DELETE");

        InternalUserValidationResponse validationResponse = InternalUserValidationResponse.builder()
                .username("permuser")
                .email("perm@example.com")
                .enabled(true)
                .locked(false)
                .roles(new HashSet<>())
                .permissions(permissions)
                .build();

        assertEquals(4, validationResponse.getPermissions().size());
    }

    @Test
    @DisplayName("Should use no-args constructor")
    void testNoArgsConstructor() {
        InternalUserValidationResponse emptyResponse = new InternalUserValidationResponse();
        assertNotNull(emptyResponse);
        assertNull(emptyResponse.getUsername());
        assertNull(emptyResponse.getEmail());
    }

    @Test
    @DisplayName("Should use all-args constructor")
    void testAllArgsConstructor() {
        Set<String> roles = Set.of("ADMIN");
        Set<String> permissions = Set.of("USER_READ");

        InternalUserValidationResponse fullResponse = new InternalUserValidationResponse(
                "testuser",
                "test@example.com",
                true,
                false,
                roles,
                permissions
        );

        assertEquals("testuser", fullResponse.getUsername());
        assertEquals("test@example.com", fullResponse.getEmail());
        assertTrue(fullResponse.getEnabled());
        assertFalse(fullResponse.getLocked());
    }
}
