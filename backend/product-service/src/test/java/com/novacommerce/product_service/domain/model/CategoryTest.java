package com.novacommerce.product_service.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Category Domain Model Tests")
class CategoryTest {

    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronic devices and accessories")
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("Should create category with all required fields")
    void testCategoryCreation() {
        assertNotNull(category);
        assertEquals(1L, category.getId());
        assertEquals("Electronics", category.getName());
        assertEquals("Electronic devices and accessories", category.getDescription());
        assertEquals("ACTIVE", category.getStatus());
    }

    @Test
    @DisplayName("Should allow setting category name")
    void testSetName() {
        category.setName("Computing");
        assertEquals("Computing", category.getName());
    }

    @Test
    @DisplayName("Should allow setting category description")
    void testSetDescription() {
        String newDescription = "Computing devices";
        category.setDescription(newDescription);
        assertEquals(newDescription, category.getDescription());
    }

    @Test
    @DisplayName("Should allow setting category status")
    void testSetStatus() {
        category.setStatus("INACTIVE");
        assertEquals("INACTIVE", category.getStatus());
    }

    @Test
    @DisplayName("Should allow setting category id")
    void testSetId() {
        category.setId(5L);
        assertEquals(5L, category.getId());
    }

    @Test
    @DisplayName("Should handle null description")
    void testNullDescription() {
        category.setDescription(null);
        assertNull(category.getDescription());
    }

    @Test
    @DisplayName("Should use builder pattern correctly")
    void testBuilderPattern() {
        Category builtCategory = Category.builder()
                .id(2L)
                .name("Clothing")
                .description("Clothing and fashion")
                .status("ACTIVE")
                .build();

        assertEquals(2L, builtCategory.getId());
        assertEquals("Clothing", builtCategory.getName());
        assertEquals("Clothing and fashion", builtCategory.getDescription());
        assertEquals("ACTIVE", builtCategory.getStatus());
    }

    @Test
    @DisplayName("Should create category with minimal fields using builder")
    void testBuilderWithMinimalFields() {
        Category minimalCategory = Category.builder()
                .id(3L)
                .name("Books")
                .build();

        assertEquals(3L, minimalCategory.getId());
        assertEquals("Books", minimalCategory.getName());
        assertNull(minimalCategory.getDescription());
        assertNull(minimalCategory.getStatus());
    }

    @Test
    @DisplayName("Should create category with no-args constructor")
    void testNoArgsConstructor() {
        Category emptyCategory = new Category();
        assertNotNull(emptyCategory);
        assertNull(emptyCategory.getId());
        assertNull(emptyCategory.getName());
    }

    @Test
    @DisplayName("Should handle all-args constructor")
    void testAllArgsConstructor() {
        Category allArgsCategory = new Category(4L, "Furniture", "Home furniture", "ACTIVE");
        assertEquals(4L, allArgsCategory.getId());
        assertEquals("Furniture", allArgsCategory.getName());
        assertEquals("Home furniture", allArgsCategory.getDescription());
        assertEquals("ACTIVE", allArgsCategory.getStatus());
    }

    @Test
    @DisplayName("Should have correct equals and hashCode")
    void testEqualsAndHashCode() {
        Category category1 = new Category(1L, "Electronics", "Devices", "ACTIVE");
        Category category2 = new Category(1L, "Electronics", "Devices", "ACTIVE");

        assertEquals(category1, category2);
        assertEquals(category1.hashCode(), category2.hashCode());
    }
}
