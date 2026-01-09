package com.novacommerce.auth_service.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SeedProperties Tests")
class SeedPropertiesTest {

    private SeedProperties seedProperties;

    @BeforeEach
    void setUp() {
        seedProperties = new SeedProperties();
    }

    @Test
    @DisplayName("Should create SeedProperties with default values")
    void testDefaultValues() {
        // Then
        assertNotNull(seedProperties);
        assertFalse(seedProperties.isEnabled());
        assertNotNull(seedProperties.getAdmin());
        assertEquals("admin", seedProperties.getAdmin().getUsername());
        assertEquals("admin@nova.com", seedProperties.getAdmin().getEmail());
        assertEquals("admin123", seedProperties.getAdmin().getPassword());
    }

    @Test
    @DisplayName("Should set and get enabled property")
    void testEnabledProperty() {
        // When
        seedProperties.setEnabled(true);

        // Then
        assertTrue(seedProperties.isEnabled());

        // When
        seedProperties.setEnabled(false);

        // Then
        assertFalse(seedProperties.isEnabled());
    }

    @Test
    @DisplayName("Should set and get admin configuration")
    void testAdminConfiguration() {
        // Given
        SeedProperties.AdminConfig adminConfig = new SeedProperties.AdminConfig();
        adminConfig.setUsername("superadmin");
        adminConfig.setEmail("superadmin@nova.com");
        adminConfig.setPassword("superpass123");

        // When
        seedProperties.setAdmin(adminConfig);

        // Then
        assertNotNull(seedProperties.getAdmin());
        assertEquals("superadmin", seedProperties.getAdmin().getUsername());
        assertEquals("superadmin@nova.com", seedProperties.getAdmin().getEmail());
        assertEquals("superpass123", seedProperties.getAdmin().getPassword());
    }

    @Test
    @DisplayName("Should create AdminConfig with default values")
    void testAdminConfigDefaultValues() {
        // When
        SeedProperties.AdminConfig adminConfig = new SeedProperties.AdminConfig();

        // Then
        assertNotNull(adminConfig);
        assertEquals("admin", adminConfig.getUsername());
        assertEquals("admin@nova.com", adminConfig.getEmail());
        assertEquals("admin123", adminConfig.getPassword());
    }

    @Test
    @DisplayName("Should set and get AdminConfig username")
    void testAdminConfigUsername() {
        // Given
        SeedProperties.AdminConfig adminConfig = new SeedProperties.AdminConfig();

        // When
        adminConfig.setUsername("newadmin");

        // Then
        assertEquals("newadmin", adminConfig.getUsername());
    }

    @Test
    @DisplayName("Should set and get AdminConfig email")
    void testAdminConfigEmail() {
        // Given
        SeedProperties.AdminConfig adminConfig = new SeedProperties.AdminConfig();

        // When
        adminConfig.setEmail("newemail@nova.com");

        // Then
        assertEquals("newemail@nova.com", adminConfig.getEmail());
    }

    @Test
    @DisplayName("Should set and get AdminConfig password")
    void testAdminConfigPassword() {
        // Given
        SeedProperties.AdminConfig adminConfig = new SeedProperties.AdminConfig();

        // When
        adminConfig.setPassword("newpassword");

        // Then
        assertEquals("newpassword", adminConfig.getPassword());
    }

    @Test
    @DisplayName("Should handle multiple admin config instances independently")
    void testMultipleAdminConfigInstances() {
        // Given
        SeedProperties.AdminConfig config1 = new SeedProperties.AdminConfig();
        SeedProperties.AdminConfig config2 = new SeedProperties.AdminConfig();

        // When
        config1.setUsername("admin1");
        config2.setUsername("admin2");

        // Then
        assertEquals("admin1", config1.getUsername());
        assertEquals("admin2", config2.getUsername());
        assertNotEquals(config1.getUsername(), config2.getUsername());
    }

    @Test
    @DisplayName("Should handle null values in AdminConfig")
    void testAdminConfigWithNullValues() {
        // Given
        SeedProperties.AdminConfig adminConfig = new SeedProperties.AdminConfig();

        // When
        adminConfig.setUsername(null);
        adminConfig.setEmail(null);
        adminConfig.setPassword(null);

        // Then
        assertNull(adminConfig.getUsername());
        assertNull(adminConfig.getEmail());
        assertNull(adminConfig.getPassword());
    }

    @Test
    @DisplayName("Should handle empty strings in AdminConfig")
    void testAdminConfigWithEmptyStrings() {
        // Given
        SeedProperties.AdminConfig adminConfig = new SeedProperties.AdminConfig();

        // When
        adminConfig.setUsername("");
        adminConfig.setEmail("");
        adminConfig.setPassword("");

        // Then
        assertEquals("", adminConfig.getUsername());
        assertEquals("", adminConfig.getEmail());
        assertEquals("", adminConfig.getPassword());
    }

    @Test
    @DisplayName("Should maintain independent state across SeedProperties instances")
    void testIndependentSeedPropertiesInstances() {
        // Given
        SeedProperties props1 = new SeedProperties();
        SeedProperties props2 = new SeedProperties();

        // When
        props1.setEnabled(true);
        props1.getAdmin().setUsername("admin1");

        props2.setEnabled(false);
        props2.getAdmin().setUsername("admin2");

        // Then
        assertTrue(props1.isEnabled());
        assertFalse(props2.isEnabled());
        assertEquals("admin1", props1.getAdmin().getUsername());
        assertEquals("admin2", props2.getAdmin().getUsername());
    }

    @Test
    @DisplayName("Should have Configuration annotation")
    void testHasConfigurationAnnotation() {
        // When
        boolean hasAnnotation = SeedProperties.class.isAnnotationPresent(
            org.springframework.context.annotation.Configuration.class
        );

        // Then
        assertTrue(hasAnnotation);
    }

    @Test
    @DisplayName("Should have ConfigurationProperties annotation with correct prefix")
    void testHasConfigurationPropertiesAnnotation() {
        // When
        org.springframework.boot.context.properties.ConfigurationProperties annotation =
            SeedProperties.class.getAnnotation(
                org.springframework.boot.context.properties.ConfigurationProperties.class
            );

        // Then
        assertNotNull(annotation);
        assertEquals("app.seed", annotation.prefix());
    }
}
