package com.novacommerce.user_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("OpenApiConfig Tests")
class OpenApiConfigTest {

    @Autowired
    private OpenApiConfig openApiConfig;

    @Autowired
    private OpenAPI openAPI;

    @Test
    @DisplayName("Should create OpenAPI bean")
    void testOpenAPIBean() {
        assertNotNull(openAPI);
    }

    @Test
    @DisplayName("Should have API info")
    void testApiInfo() {
        assertNotNull(openAPI.getInfo());
        assertNotNull(openAPI.getInfo().getTitle());
        assertNotNull(openAPI.getInfo().getVersion());
    }

    @Test
    @DisplayName("Should have contact information")
    void testContactInfo() {
        assertNotNull(openAPI.getInfo().getContact());
    }

    @Test
    @DisplayName("Should have servers configured")
    void testServers() {
        assertNotNull(openAPI.getServers());
        assertFalse(openAPI.getServers().isEmpty());
    }

    @Test
    @DisplayName("Should have security components")
    void testSecurityComponents() {
        assertNotNull(openAPI.getComponents());
        assertNotNull(openAPI.getComponents().getSecuritySchemes());
    }

    @Test
    @DisplayName("Should have bearer authentication scheme")
    void testBearerAuth() {
        assertNotNull(openAPI.getComponents().getSecuritySchemes().get("bearerAuth"));
    }

    @Test
    @DisplayName("Should have security requirement")
    void testSecurityRequirement() {
        assertNotNull(openAPI.getSecurity());
        assertFalse(openAPI.getSecurity().isEmpty());
    }

    @Test
    @DisplayName("Should have license information")
    void testLicenseInfo() {
        assertNotNull(openAPI.getInfo().getLicense());
    }

    @Test
    @DisplayName("Should have description")
    void testDescription() {
        assertNotNull(openAPI.getInfo().getDescription());
    }

    @Test
    @DisplayName("Should have OpenApiConfig bean")
    void testOpenApiConfigBean() {
        assertNotNull(openApiConfig);
    }
}
