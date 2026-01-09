package com.novacommerce.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableConfigurationProperties(SecurityProperties.class)
@TestPropertySource(properties = {
    "gateway.public-paths[0]=/api/auth/login",
    "gateway.public-paths[1]=/api/auth/register",
    "gateway.public-paths[2]=/actuator/**"
})
class SecurityPropertiesTest {

    @Test
    void testSecurityPropertiesClass() {
        SecurityProperties props = new SecurityProperties();
        assertNotNull(props, "SecurityProperties should be instantiable");
    }

    @Test
    void testPublicPathsInitialization() {
        SecurityProperties props = new SecurityProperties();
        assertNotNull(props.getPublicPaths(), "Public paths should be initialized");
    }

    @Test
    void testAddPublicPath() {
        SecurityProperties props = new SecurityProperties();
        props.getPublicPaths().add("/api/health");
        assertEquals(1, props.getPublicPaths().size(), "Should have 1 public path");
    }

    @Test
    void testMultiplePublicPaths() {
        SecurityProperties props = new SecurityProperties();
        props.getPublicPaths().add("/api/auth/**");
        props.getPublicPaths().add("/api/health");
        props.getPublicPaths().add("/actuator/**");
        
        assertEquals(3, props.getPublicPaths().size(), "Should have 3 public paths");
    }

    @Test
    void testRemovePublicPath() {
        SecurityProperties props = new SecurityProperties();
        props.getPublicPaths().add("/api/auth/**");
        props.getPublicPaths().add("/api/health");
        
        props.getPublicPaths().remove("/api/auth/**");
        assertEquals(1, props.getPublicPaths().size(), "Should have 1 public path after removal");
    }

    @Test
    void testClearPublicPaths() {
        SecurityProperties props = new SecurityProperties();
        props.getPublicPaths().add("/api/auth/**");
        props.getPublicPaths().add("/api/health");
        
        props.getPublicPaths().clear();
        assertEquals(0, props.getPublicPaths().size(), "Should have 0 public paths after clear");
    }
}
