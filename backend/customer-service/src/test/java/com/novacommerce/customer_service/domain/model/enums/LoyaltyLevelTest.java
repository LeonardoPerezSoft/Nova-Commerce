package com.novacommerce.customer_service.domain.model.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoyaltyLevelTest")
class LoyaltyLevelTest {

    @Test
    @DisplayName("givenLoyaltyLevel_whenValuesMethod_thenReturnAllLevels")
    void givenLoyaltyLevel_whenValuesMethod_thenReturnAllLevels() {
        // GIVEN & WHEN
        LoyaltyLevel[] levels = LoyaltyLevel.values();

        // THEN
        assertNotNull(levels);
        assertEquals(4, levels.length);
    }

    @Test
    @DisplayName("givenLoyaltyLevel_whenCheckBronze_thenExists")
    void givenLoyaltyLevel_whenCheckBronze_thenExists() {
        // GIVEN & WHEN & THEN
        assertEquals(LoyaltyLevel.BRONZE, LoyaltyLevel.valueOf("BRONZE"));
    }

    @Test
    @DisplayName("givenLoyaltyLevel_whenCheckSilver_thenExists")
    void givenLoyaltyLevel_whenCheckSilver_thenExists() {
        // GIVEN & WHEN & THEN
        assertEquals(LoyaltyLevel.SILVER, LoyaltyLevel.valueOf("SILVER"));
    }

    @Test
    @DisplayName("givenLoyaltyLevel_whenCheckGold_thenExists")
    void givenLoyaltyLevel_whenCheckGold_thenExists() {
        // GIVEN & WHEN & THEN
        assertEquals(LoyaltyLevel.GOLD, LoyaltyLevel.valueOf("GOLD"));
    }

    @Test
    @DisplayName("givenLoyaltyLevel_whenCheckPlatinum_thenExists")
    void givenLoyaltyLevel_whenCheckPlatinum_thenExists() {
        // GIVEN & WHEN & THEN
        assertEquals(LoyaltyLevel.PLATINUM, LoyaltyLevel.valueOf("PLATINUM"));
    }

    @Test
    @DisplayName("givenInvalidLevel_whenValueOf_thenThrowException")
    void givenInvalidLevel_whenValueOf_thenThrowException() {
        // GIVEN & WHEN & THEN
        assertThrows(IllegalArgumentException.class, () -> LoyaltyLevel.valueOf("DIAMOND"));
    }

    @Test
    @DisplayName("givenLevelsAreNotNull_whenCreated_thenEachLevelNotNull")
    void givenLevelsAreNotNull_whenCreated_thenEachLevelNotNull() {
        // GIVEN & WHEN & THEN
        for (LoyaltyLevel level : LoyaltyLevel.values()) {
            assertNotNull(level);
        }
    }

    @Test
    @DisplayName("givenMultipleLevels_whenCompare_thenProperEquality")
    void givenMultipleLevels_whenCompare_thenProperEquality() {
        // GIVEN & WHEN & THEN
        assertEquals(LoyaltyLevel.BRONZE, LoyaltyLevel.BRONZE);
        assertNotEquals(LoyaltyLevel.BRONZE, LoyaltyLevel.GOLD);
        assertNotEquals(LoyaltyLevel.SILVER, LoyaltyLevel.PLATINUM);
    }

    @Test
    @DisplayName("givenLevel_whenNameMethod_thenReturnCorrectName")
    void givenLevel_whenNameMethod_thenReturnCorrectName() {
        // GIVEN & WHEN & THEN
        assertEquals("BRONZE", LoyaltyLevel.BRONZE.name());
        assertEquals("SILVER", LoyaltyLevel.SILVER.name());
        assertEquals("GOLD", LoyaltyLevel.GOLD.name());
        assertEquals("PLATINUM", LoyaltyLevel.PLATINUM.name());
    }

    @Test
    @DisplayName("givenLoyaltyLevels_whenOrdinalValues_thenIncremental")
    void givenLoyaltyLevels_whenOrdinalValues_thenIncremental() {
        // GIVEN & WHEN & THEN
        assertTrue(LoyaltyLevel.BRONZE.ordinal() < LoyaltyLevel.SILVER.ordinal());
        assertTrue(LoyaltyLevel.SILVER.ordinal() < LoyaltyLevel.GOLD.ordinal());
        assertTrue(LoyaltyLevel.GOLD.ordinal() < LoyaltyLevel.PLATINUM.ordinal());
    }
}
