package com.novacommerce.user_service.web.api.dto.response;

import com.novacommerce.user_service.domain.enums.UserStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserResponse DTO Tests")
class UserResponseTest {

    private UserResponse userResponse;
    private UUID userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        createdAt = LocalDateTime.now().minusDays(10);
        updatedAt = LocalDateTime.now();

        RoleResponse roleResponse = new RoleResponse(
                UUID.randomUUID(),
                "ADMIN",
                "Administrator role",
                new HashSet<>()
        );

        Set<RoleResponse> roles = new HashSet<>();
        roles.add(roleResponse);

        userResponse = new UserResponse(
                userId,
                "testuser",
                "test@example.com",
                UserStatusEnum.ACTIVE.name(),
                true,
                false,
                roles,
                createdAt,
                updatedAt
        );
    }

    @Test
    @DisplayName("Should create UserResponse with all fields")
    void testUserResponseCreation() {
        assertNotNull(userResponse);
        assertEquals(userId, userResponse.id());
        assertEquals("testuser", userResponse.username());
        assertEquals("test@example.com", userResponse.email());
        assertEquals("ACTIVE", userResponse.status());
        assertTrue(userResponse.enabled());
        assertFalse(userResponse.locked());
        assertNotNull(userResponse.roles());
        assertEquals(1, userResponse.roles().size());
        assertEquals(createdAt, userResponse.createdAt());
        assertEquals(updatedAt, userResponse.updatedAt());
    }

    @Test
    @DisplayName("Should handle null roles")
    void testNullRoles() {
        UserResponse response = new UserResponse(
                UUID.randomUUID(),
                "testuser",
                "test@example.com",
                "ACTIVE",
                true,
                false,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        assertNull(response.roles());
    }

    @Test
    @DisplayName("Should handle empty roles")
    void testEmptyRoles() {
        UserResponse response = new UserResponse(
                UUID.randomUUID(),
                "testuser",
                "test@example.com",
                "ACTIVE",
                true,
                false,
                new HashSet<>(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        assertNotNull(response.roles());
        assertTrue(response.roles().isEmpty());
    }

    @Test
    @DisplayName("Should handle multiple roles")
    void testMultipleRoles() {
        Set<RoleResponse> roles = new HashSet<>();
        roles.add(new RoleResponse(UUID.randomUUID(), "ADMIN", "Admin", new HashSet<>()));
        roles.add(new RoleResponse(UUID.randomUUID(), "USER", "User", new HashSet<>()));

        UserResponse response = new UserResponse(
                UUID.randomUUID(),
                "testuser",
                "test@example.com",
                "ACTIVE",
                true,
                false,
                roles,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        assertEquals(2, response.roles().size());
    }

    @Test
    @DisplayName("Should handle different statuses")
    void testDifferentStatuses() {
        UserResponse activeUser = new UserResponse(
                UUID.randomUUID(), "user1", "email1@test.com", "ACTIVE", true, false, new HashSet<>(), LocalDateTime.now(), LocalDateTime.now()
        );
        assertEquals("ACTIVE", activeUser.status());

        UserResponse inactiveUser = new UserResponse(
                UUID.randomUUID(), "user2", "email2@test.com", "INACTIVE", false, false, new HashSet<>(), LocalDateTime.now(), LocalDateTime.now()
        );
        assertEquals("INACTIVE", inactiveUser.status());

        UserResponse lockedUser = new UserResponse(
                UUID.randomUUID(), "user3", "email3@test.com", "LOCKED", true, true, new HashSet<>(), LocalDateTime.now(), LocalDateTime.now()
        );
        assertEquals("LOCKED", lockedUser.status());
    }

    @Test
    @DisplayName("Should handle enabled and locked combinations")
    void testEnabledLockedCombinations() {
        UserResponse user1 = new UserResponse(
                UUID.randomUUID(), "user1", "email@test.com", "ACTIVE", true, false, new HashSet<>(), LocalDateTime.now(), LocalDateTime.now()
        );
        assertTrue(user1.enabled());
        assertFalse(user1.locked());

        UserResponse user2 = new UserResponse(
                UUID.randomUUID(), "user2", "email@test.com", "INACTIVE", false, false, new HashSet<>(), LocalDateTime.now(), LocalDateTime.now()
        );
        assertFalse(user2.enabled());
        assertFalse(user2.locked());

        UserResponse user3 = new UserResponse(
                UUID.randomUUID(), "user3", "email@test.com", "LOCKED", true, true, new HashSet<>(), LocalDateTime.now(), LocalDateTime.now()
        );
        assertTrue(user3.enabled());
        assertTrue(user3.locked());
    }

    @Test
    @DisplayName("Should preserve UUID format")
    void testUUIDFormat() {
        UUID testId = UUID.randomUUID();
        UserResponse response = new UserResponse(
                testId, "testuser", "test@example.com", "ACTIVE", true, false, new HashSet<>(), LocalDateTime.now(), LocalDateTime.now()
        );

        assertEquals(testId, response.id());
        assertEquals(testId.toString(), response.id().toString());
    }

    @Test
    @DisplayName("Should preserve timestamps")
    void testTimestamps() {
        LocalDateTime created = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime updated = LocalDateTime.of(2024, 1, 15, 14, 30);

        UserResponse response = new UserResponse(
                UUID.randomUUID(), "testuser", "test@example.com", "ACTIVE", true, false, new HashSet<>(), created, updated
        );

        assertEquals(created, response.createdAt());
        assertEquals(updated, response.updatedAt());
    }

    @Test
    @DisplayName("Should handle username and email correctly")
    void testUsernameAndEmail() {
        UserResponse response = new UserResponse(
                UUID.randomUUID(), "admin", "admin@novacommerce.com", "ACTIVE", true, false, new HashSet<>(), LocalDateTime.now(), LocalDateTime.now()
        );

        assertEquals("admin", response.username());
        assertEquals("admin@novacommerce.com", response.email());
    }
}
