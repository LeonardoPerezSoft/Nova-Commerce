package com.novacommerce.user_service.domain.model;

import com.novacommerce.user_service.domain.enums.UserStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Domain Model Tests")
class UserTest {

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        role = Role.builder()
                .id(UUID.randomUUID())
                .name("ADMIN")
                .description("Administrator role")
                .build();

        user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .password("encryptedPassword123")
                .status(UserStatusEnum.ACTIVE)
                .enabled(true)
                .locked(false)
                .roles(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .lastLogin(LocalDateTime.now().minusDays(1))
                .build();
    }

    @Test
    @DisplayName("Should create user with all required fields")
    void testUserCreation() {
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("encryptedPassword123", user.getPassword());
        assertEquals(UserStatusEnum.ACTIVE, user.getStatus());
        assertTrue(user.getEnabled());
        assertFalse(user.getLocked());
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    @DisplayName("Should allow setting username")
    void testSetUsername() {
        user.setUsername("newusername");
        assertEquals("newusername", user.getUsername());
    }

    @Test
    @DisplayName("Should allow setting email")
    void testSetEmail() {
        user.setEmail("newemail@example.com");
        assertEquals("newemail@example.com", user.getEmail());
    }

    @Test
    @DisplayName("Should allow setting password")
    void testSetPassword() {
        user.setPassword("newEncryptedPassword");
        assertEquals("newEncryptedPassword", user.getPassword());
    }

    @Test
    @DisplayName("Should allow setting status")
    void testSetStatus() {
        user.setStatus(UserStatusEnum.INACTIVE);
        assertEquals(UserStatusEnum.INACTIVE, user.getStatus());
    }

    @Test
    @DisplayName("Should allow enabling/disabling user")
    void testSetEnabled() {
        user.setEnabled(false);
        assertFalse(user.getEnabled());
        
        user.setEnabled(true);
        assertTrue(user.getEnabled());
    }

    @Test
    @DisplayName("Should allow locking/unlocking user")
    void testSetLocked() {
        user.setLocked(true);
        assertTrue(user.getLocked());
        
        user.setLocked(false);
        assertFalse(user.getLocked());
    }

    @Test
    @DisplayName("Should add role to user successfully")
    void testAddRole() {
        user.addRole(role);
        
        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains(role));
    }

    @Test
    @DisplayName("Should not add null role")
    void testAddNullRole() {
        user.addRole(null);
        
        assertEquals(0, user.getRoles().size());
    }

    @Test
    @DisplayName("Should remove role from user successfully")
    void testRemoveRole() {
        user.addRole(role);
        assertEquals(1, user.getRoles().size());
        
        user.removeRole(role);
        assertEquals(0, user.getRoles().size());
        assertFalse(user.getRoles().contains(role));
    }

    @Test
    @DisplayName("Should not fail when removing null role")
    void testRemoveNullRole() {
        user.addRole(role);
        assertEquals(1, user.getRoles().size());
        
        user.removeRole(null);
        assertEquals(1, user.getRoles().size());
    }

    @Test
    @DisplayName("Should use builder pattern correctly")
    void testBuilderPattern() {
        UUID testId = UUID.randomUUID();
        User builtUser = User.builder()
                .id(testId)
                .username("builderUser")
                .email("builder@example.com")
                .password("pass123")
                .status(UserStatusEnum.LOCKED)
                .enabled(false)
                .locked(true)
                .build();

        assertEquals(testId, builtUser.getId());
        assertEquals("builderUser", builtUser.getUsername());
        assertEquals("builder@example.com", builtUser.getEmail());
        assertEquals(UserStatusEnum.LOCKED, builtUser.getStatus());
        assertFalse(builtUser.getEnabled());
        assertTrue(builtUser.getLocked());
    }

    @Test
    @DisplayName("Should set last login timestamp")
    void testSetLastLogin() {
        LocalDateTime loginTime = LocalDateTime.now();
        user.setLastLogin(loginTime);
        assertEquals(loginTime, user.getLastLogin());
    }

    @Test
    @DisplayName("Should handle multiple roles")
    void testMultipleRoles() {
        Role role1 = Role.builder().id(UUID.randomUUID()).name("ADMIN").build();
        Role role2 = Role.builder().id(UUID.randomUUID()).name("USER").build();
        Role role3 = Role.builder().id(UUID.randomUUID()).name("SALES").build();

        user.addRole(role1);
        user.addRole(role2);
        user.addRole(role3);

        assertEquals(3, user.getRoles().size());
        assertTrue(user.getRoles().contains(role1));
        assertTrue(user.getRoles().contains(role2));
        assertTrue(user.getRoles().contains(role3));
    }
}
