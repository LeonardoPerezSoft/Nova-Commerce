package com.novacommerce.product_service.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductType Enum Tests")
class ProductTypeTest {

    @Test
    @DisplayName("Should have PHYSICAL product type")
    void testPhysicalType() {
        assertEquals("PHYSICAL", ProductType.PHYSICAL.name());
        assertNotNull(ProductType.PHYSICAL);
    }

    @Test
    @DisplayName("Should have DIGITAL product type")
    void testDigitalType() {
        assertEquals("DIGITAL", ProductType.DIGITAL.name());
        assertNotNull(ProductType.DIGITAL);
    }

    @Test
    @DisplayName("Should have SERVICE product type")
    void testServiceType() {
        assertEquals("SERVICE", ProductType.SERVICE.name());
        assertNotNull(ProductType.SERVICE);
    }

    @Test
    @DisplayName("Should have SUBSCRIPTION product type")
    void testSubscriptionType() {
        assertEquals("SUBSCRIPTION", ProductType.SUBSCRIPTION.name());
        assertNotNull(ProductType.SUBSCRIPTION);
    }

    @Test
    @DisplayName("Should get all product types")
    void testGetAllTypes() {
        ProductType[] types = ProductType.values();
        assertEquals(4, types.length);
    }

    @Test
    @DisplayName("Should be able to get ProductType by name")
    void testValueOf() {
        assertEquals(ProductType.PHYSICAL, ProductType.valueOf("PHYSICAL"));
        assertEquals(ProductType.DIGITAL, ProductType.valueOf("DIGITAL"));
        assertEquals(ProductType.SERVICE, ProductType.valueOf("SERVICE"));
        assertEquals(ProductType.SUBSCRIPTION, ProductType.valueOf("SUBSCRIPTION"));
    }

    @Test
    @DisplayName("Should throw exception for invalid ProductType name")
    void testInvalidTypeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ProductType.valueOf("INVALID"));
    }

    @Test
    @DisplayName("Should have correct enum ordinal values")
    void testEnumOrdinals() {
        assertTrue(ProductType.PHYSICAL.ordinal() >= 0);
        assertTrue(ProductType.DIGITAL.ordinal() >= 0);
        assertTrue(ProductType.SERVICE.ordinal() >= 0);
        assertTrue(ProductType.SUBSCRIPTION.ordinal() >= 0);
    }

    @Test
    @DisplayName("Should be comparable using equals")
    void testEnumComparison() {
        ProductType type1 = ProductType.PHYSICAL;
        ProductType type2 = ProductType.PHYSICAL;
        assertEquals(type1, type2);
    }

    @Test
    @DisplayName("Should use enum in conditional logic")
    void testEnumConditions() {
        ProductType type = ProductType.DIGITAL;
        
        if (type == ProductType.DIGITAL) {
            assertTrue(true);
        } else {
            fail("Enum comparison failed");
        }
    }
}
