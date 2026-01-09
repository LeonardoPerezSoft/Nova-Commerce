package com.novacommerce.product_service.adapter.in.web.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CategoryRequest DTO Tests")
class CategoryRequestTest {

    private CategoryRequest request;

    @BeforeEach
    void setUp() {
        request = CategoryRequest.builder()
                .name("Electronics")
                .description("Electronic devices")
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("Should create CategoryRequest with all fields")
    void testCategoryRequestCreation() {
        assertNotNull(request);
        assertEquals("Electronics", request.getName());
        assertEquals("Electronic devices", request.getDescription());
        assertEquals("ACTIVE", request.getStatus());
    }

    @Test
    @DisplayName("Should allow setting name")
    void testSetName() {
        request.setName("Computing");
        assertEquals("Computing", request.getName());
    }

    @Test
    @DisplayName("Should allow setting description")
    void testSetDescription() {
        request.setDescription("Computing devices and accessories");
        assertEquals("Computing devices and accessories", request.getDescription());
    }

    @Test
    @DisplayName("Should allow setting status")
    void testSetStatus() {
        request.setStatus("INACTIVE");
        assertEquals("INACTIVE", request.getStatus());
    }

    @Test
    @DisplayName("Should handle null description")
    void testNullDescription() {
        request.setDescription(null);
        assertNull(request.getDescription());
    }

    @Test
    @DisplayName("Should use builder pattern")
    void testBuilderPattern() {
        CategoryRequest built = CategoryRequest.builder()
                .name("Clothing")
                .description("Clothing and fashion")
                .status("ACTIVE")
                .build();

        assertEquals("Clothing", built.getName());
        assertEquals("Clothing and fashion", built.getDescription());
        assertEquals("ACTIVE", built.getStatus());
    }

    @Test
    @DisplayName("Should handle empty description")
    void testEmptyDescription() {
        request.setDescription("");
        assertEquals("", request.getDescription());
    }

    @Test
    @DisplayName("Should handle different statuses")
    void testDifferentStatuses() {
        request.setStatus("ACTIVE");
        assertEquals("ACTIVE", request.getStatus());

        request.setStatus("INACTIVE");
        assertEquals("INACTIVE", request.getStatus());
    }

    @Test
    @DisplayName("Should create with no-args constructor")
    void testNoArgsConstructor() {
        CategoryRequest empty = new CategoryRequest();
        assertNotNull(empty);
    }

    @Test
    @DisplayName("Should create with all-args constructor")
    void testAllArgsConstructor() {
        CategoryRequest allArgs = new CategoryRequest(
                "Books",
                "Books and media",
                "ACTIVE"
        );
        assertEquals("Books", allArgs.getName());
        assertEquals("Books and media", allArgs.getDescription());
        assertEquals("ACTIVE", allArgs.getStatus());
    }
}
