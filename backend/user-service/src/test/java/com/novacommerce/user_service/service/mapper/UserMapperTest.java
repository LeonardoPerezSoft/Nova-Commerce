package com.novacommerce.user_service.service.mapper;

import com.novacommerce.user_service.domain.enums.UserStatusEnum;
import com.novacommerce.user_service.domain.model.Permission;
import com.novacommerce.user_service.domain.model.Role;
import com.novacommerce.user_service.domain.model.User;
import com.novacommerce.user_service.web.api.dto.response.UserResponse;
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
@DisplayName("UserMapper Tests")
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    private User user;
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

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .password("encryptedPassword")
                .status(UserStatusEnum.ACTIVE)
                .enabled(true)
                .locked(false)
                .roles(roles)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .lastLogin(LocalDateTime.now().minusDays(1))
                .build();
    }

    @Test
    @DisplayName("Should map User to UserResponse")
    void testUserToUserResponse() {
        UserResponse response = userMapper.userToUserResponse(user);

        assertNotNull(response);
        assertEquals(user.getId(), response.id());
        assertEquals(user.getUsername(), response.username());
        assertEquals(user.getEmail(), response.email());
        assertEquals(user.getStatus().name(), response.status());
        assertEquals(user.getEnabled(), response.enabled());
        assertEquals(user.getLocked(), response.locked());
        assertEquals(user.getCreatedAt(), response.createdAt());
        assertEquals(user.getUpdatedAt(), response.updatedAt());
    }

    @Test
    @DisplayName("Should map roles correctly")
    void testRolesMapping() {
        UserResponse response = userMapper.userToUserResponse(user);

        assertNotNull(response.roles());
        assertEquals(1, response.roles().size());
        assertTrue(response.roles().stream()
                .anyMatch(r -> r.name().equals("ADMIN")));
    }

    @Test
    @DisplayName("Should map nested permissions in roles")
    void testNestedPermissionsMapping() {
        UserResponse response = userMapper.userToUserResponse(user);

        assertNotNull(response.roles());
        response.roles().forEach(roleResponse -> {
            assertNotNull(roleResponse.permissions());
            assertEquals(1, roleResponse.permissions().size());
            assertTrue(roleResponse.permissions().stream()
                    .anyMatch(p -> p.name().equals("USER_READ")));
        });
    }

    @Test
    @DisplayName("Should handle null user")
    void testNullUser() {
        UserResponse response = userMapper.userToUserResponse(null);
        assertNull(response);
    }

    @Test
    @DisplayName("Should handle user without roles")
    void testUserWithoutRoles() {
        User userWithoutRoles = User.builder()
                .id(UUID.randomUUID())
                .username("noroles")
                .email("noroles@example.com")
                .password("password")
                .status(UserStatusEnum.ACTIVE)
                .enabled(true)
                .locked(false)
                .roles(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UserResponse response = userMapper.userToUserResponse(userWithoutRoles);

        assertNotNull(response);
        assertNotNull(response.roles());
        assertTrue(response.roles().isEmpty());
    }

    @Test
    @DisplayName("Should handle different user statuses")
    void testDifferentStatuses() {
        user.setStatus(UserStatusEnum.INACTIVE);
        UserResponse inactiveResponse = userMapper.userToUserResponse(user);
        assertEquals("INACTIVE", inactiveResponse.status());

        user.setStatus(UserStatusEnum.LOCKED);
        UserResponse lockedResponse = userMapper.userToUserResponse(user);
        assertEquals("LOCKED", lockedResponse.status());

        user.setStatus(UserStatusEnum.ACTIVE);
        UserResponse activeResponse = userMapper.userToUserResponse(user);
        assertEquals("ACTIVE", activeResponse.status());
    }

    @Test
    @DisplayName("Should map enabled and locked flags correctly")
    void testEnabledAndLockedMapping() {
        user.setEnabled(false);
        user.setLocked(true);

        UserResponse response = userMapper.userToUserResponse(user);

        assertFalse(response.enabled());
        assertTrue(response.locked());
    }

    @Test
    @DisplayName("Should handle multiple roles")
    void testMultipleRoles() {
        Role salesRole = Role.builder()
                .id(UUID.randomUUID())
                .name("SALES")
                .description("Sales role")
                .permissions(new HashSet<>())
                .build();

        user.addRole(salesRole);

        UserResponse response = userMapper.userToUserResponse(user);

        assertEquals(2, response.roles().size());
        assertTrue(response.roles().stream()
                .anyMatch(r -> r.name().equals("ADMIN")));
        assertTrue(response.roles().stream()
                .anyMatch(r -> r.name().equals("SALES")));
    }

    @Test
    @DisplayName("Should preserve timestamps")
    void testTimestampMapping() {
        LocalDateTime created = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime updated = LocalDateTime.of(2024, 1, 15, 14, 30);

        user.setCreatedAt(created);
        user.setUpdatedAt(updated);

        UserResponse response = userMapper.userToUserResponse(user);

        assertEquals(created, response.createdAt());
        assertEquals(updated, response.updatedAt());
    }

    @Test
    @DisplayName("Should map UUID correctly")
    void testUUIDMapping() {
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        UserResponse response = userMapper.userToUserResponse(user);

        assertEquals(userId, response.id());
    }
}
