package com.novacommerce.customer_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OpenApiConfig Tests")
class OpenApiConfigTest {

    @Test
    @DisplayName("givenConfig_whenCreateOpenAPI_thenCorrectMetadata")
    void givenConfig_whenCreateOpenAPI_thenCorrectMetadata() {
        // GIVEN
        OpenApiConfig config = new OpenApiConfig();
        String appName = "Customer Service";
        String appDescription = "Servicio de gestión de clientes";
        String appVersion = "1.0.0";
        String contactName = "Nova Commerce";
        String contactEmail = "contact@novacommerce.com";
        String contactUrl = "https://novacommerce.com";

        // WHEN
        OpenAPI openAPI = config.customOpenAPI(appName, appDescription, appVersion, contactName, contactEmail, contactUrl);

        // THEN
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        
        Info info = openAPI.getInfo();
        assertEquals(appName, info.getTitle());
        assertEquals(appDescription, info.getDescription());
        assertEquals(appVersion, info.getVersion());
        
        Contact contact = info.getContact();
        assertNotNull(contact);
        assertEquals(contactName, contact.getName());
        assertEquals(contactEmail, contact.getEmail());
        assertEquals(contactUrl, contact.getUrl());
    }

    @Test
    @DisplayName("givenDefaultValues_whenCreateOpenAPI_thenUseDefaults")
    void givenDefaultValues_whenCreateOpenAPI_thenUseDefaults() {
        // GIVEN
        OpenApiConfig config = new OpenApiConfig();

        // WHEN
        OpenAPI openAPI = config.customOpenAPI(
            "Test Service",
            "Test Description",
            "2.0.0",
            "Test Team",
            "test@test.com",
            "https://test.com"
        );

        // THEN
        assertNotNull(openAPI);
        assertEquals("Test Service", openAPI.getInfo().getTitle());
        assertEquals("Test Description", openAPI.getInfo().getDescription());
        assertEquals("2.0.0", openAPI.getInfo().getVersion());
    }

    @Test
    @DisplayName("givenMinimalConfig_whenCreateOpenAPI_thenAllFieldsPresent")
    void givenMinimalConfig_whenCreateOpenAPI_thenAllFieldsPresent() {
        // GIVEN
        OpenApiConfig config = new OpenApiConfig();

        // WHEN
        OpenAPI openAPI = config.customOpenAPI(
            "App",
            "Desc",
            "1.0",
            "Name",
            "email@test.com",
            "http://url.com"
        );

        // THEN
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertNotNull(openAPI.getInfo().getTitle());
        assertNotNull(openAPI.getInfo().getDescription());
        assertNotNull(openAPI.getInfo().getVersion());
        assertNotNull(openAPI.getInfo().getContact());
        assertNotNull(openAPI.getInfo().getContact().getName());
        assertNotNull(openAPI.getInfo().getContact().getEmail());
        assertNotNull(openAPI.getInfo().getContact().getUrl());
    }

    @Test
    @DisplayName("givenConfig_whenCreateMultipleInstances_thenDifferentObjects")
    void givenConfig_whenCreateMultipleInstances_thenDifferentObjects() {
        // GIVEN
        OpenApiConfig config = new OpenApiConfig();

        // WHEN
        OpenAPI openAPI1 = config.customOpenAPI("App1", "Desc1", "1.0", "Name1", "email1@test.com", "http://url1.com");
        OpenAPI openAPI2 = config.customOpenAPI("App2", "Desc2", "2.0", "Name2", "email2@test.com", "http://url2.com");

        // THEN
        assertNotSame(openAPI1, openAPI2);
        assertNotEquals(openAPI1.getInfo().getTitle(), openAPI2.getInfo().getTitle());
        assertNotEquals(openAPI1.getInfo().getVersion(), openAPI2.getInfo().getVersion());
    }
}
