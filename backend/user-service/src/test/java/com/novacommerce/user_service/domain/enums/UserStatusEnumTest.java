package com.novacommerce.user_service.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserStatusEnum Tests")
class UserStatusEnumTest {

    @Test
    @DisplayName("Should have ACTIVE status")
    void testActiveStatus() {
        UserStatusEnum status = UserStatusEnum.ACTIVE;
        assertNotNull(status);
        assertEquals("ACTIVE", status.name());
        assertEquals("Usuario activo", status.getDescription());
    }

    @Test
    @DisplayName("Should have INACTIVE status")
    void testInactiveStatus() {
        UserStatusEnum status = UserStatusEnum.INACTIVE;
        assertNotNull(status);
        assertEquals("INACTIVE", status.name());
        assertEquals("Usuario inactivo", status.getDescription());
    }

    @Test
    @DisplayName("Should have LOCKED status")
    void testLockedStatus() {
        UserStatusEnum status = UserStatusEnum.LOCKED;
        assertNotNull(status);
        assertEquals("LOCKED", status.name());
        assertEquals("Usuario bloqueado", status.getDescription());
    }

    @Test
    @DisplayName("Should convert string to enum")
    void testValueOf() {
        UserStatusEnum active = UserStatusEnum.valueOf("ACTIVE");
        assertEquals(UserStatusEnum.ACTIVE, active);

        UserStatusEnum inactive = UserStatusEnum.valueOf("INACTIVE");
        assertEquals(UserStatusEnum.INACTIVE, inactive);

        UserStatusEnum locked = UserStatusEnum.valueOf("LOCKED");
        assertEquals(UserStatusEnum.LOCKED, locked);
    }

    @Test
    @DisplayName("Should throw exception for invalid value")
    void testInvalidValueOf() {
        assertThrows(IllegalArgumentException.class, () -> {
            UserStatusEnum.valueOf("INVALID_STATUS");
        });
    }

    @Test
    @DisplayName("Should have exactly 3 enum values")
    void testEnumValuesCount() {
        UserStatusEnum[] values = UserStatusEnum.values();
        assertEquals(3, values.length);
    }

    @Test
    @DisplayName("Should contain all expected values")
    void testAllValues() {
        UserStatusEnum[] values = UserStatusEnum.values();
        
        assertEquals(UserStatusEnum.ACTIVE, values[0]);
        assertEquals(UserStatusEnum.INACTIVE, values[1]);
        assertEquals(UserStatusEnum.LOCKED, values[2]);
    }

    @Test
    @DisplayName("Should return correct description for each status")
    void testDescriptions() {
        assertEquals("Usuario activo", UserStatusEnum.ACTIVE.getDescription());
        assertEquals("Usuario inactivo", UserStatusEnum.INACTIVE.getDescription());
        assertEquals("Usuario bloqueado", UserStatusEnum.LOCKED.getDescription());
    }

    @Test
    @DisplayName("Should be case sensitive")
    void testCaseSensitivity() {
        assertThrows(IllegalArgumentException.class, () -> {
            UserStatusEnum.valueOf("active");
        });
    }

    @Test
    @DisplayName("Should compare enum values correctly")
    void testEnumComparison() {
        UserStatusEnum status1 = UserStatusEnum.ACTIVE;
        UserStatusEnum status2 = UserStatusEnum.ACTIVE;
        UserStatusEnum status3 = UserStatusEnum.LOCKED;

        assertEquals(status1, status2);
        assertNotEquals(status1, status3);
        assertSame(status1, status2);
    }
}
