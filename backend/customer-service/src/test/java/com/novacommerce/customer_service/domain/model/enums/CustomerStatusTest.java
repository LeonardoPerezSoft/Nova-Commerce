package com.novacommerce.customer_service.domain.model.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CustomerStatusTest")
class CustomerStatusTest {

    @Test
    @DisplayName("givenCustomerStatus_whenValuesMethod_thenReturnAllStatuses")
    void givenCustomerStatus_whenValuesMethod_thenReturnAllStatuses() {
        // GIVEN & WHEN
        CustomerStatus[] statuses = CustomerStatus.values();

        // THEN
        assertNotNull(statuses);
        assertEquals(3, statuses.length);
    }

    @Test
    @DisplayName("givenCustomerStatus_whenCheckActive_thenExists")
    void givenCustomerStatus_whenCheckActive_thenExists() {
        // GIVEN & WHEN & THEN
        assertEquals(CustomerStatus.ACTIVE, CustomerStatus.valueOf("ACTIVE"));
    }

    @Test
    @DisplayName("givenCustomerStatus_whenCheckInactive_thenExists")
    void givenCustomerStatus_whenCheckInactive_thenExists() {
        // GIVEN & WHEN & THEN
        assertEquals(CustomerStatus.INACTIVE, CustomerStatus.valueOf("INACTIVE"));
    }

    @Test
    @DisplayName("givenCustomerStatus_whenCheckBlocked_thenExists")
    void givenCustomerStatus_whenCheckBlocked_thenExists() {
        // GIVEN & WHEN & THEN
        assertEquals(CustomerStatus.BLOCKED, CustomerStatus.valueOf("BLOCKED"));
    }

    @Test
    @DisplayName("givenInvalidStatus_whenValueOf_thenThrowException")
    void givenInvalidStatus_whenValueOf_thenThrowException() {
        // GIVEN & WHEN & THEN
        assertThrows(IllegalArgumentException.class, () -> CustomerStatus.valueOf("INVALID"));
    }

    @Test
    @DisplayName("givenStatusesAreNotNull_whenCreated_thenEachStatusNotNull")
    void givenStatusesAreNotNull_whenCreated_thenEachStatusNotNull() {
        // GIVEN & WHEN & THEN
        for (CustomerStatus status : CustomerStatus.values()) {
            assertNotNull(status);
        }
    }

    @Test
    @DisplayName("givenMultipleStatuses_whenCompare_thenProperEquality")
    void givenMultipleStatuses_whenCompare_thenProperEquality() {
        // GIVEN & WHEN & THEN
        assertEquals(CustomerStatus.ACTIVE, CustomerStatus.ACTIVE);
        assertNotEquals(CustomerStatus.ACTIVE, CustomerStatus.INACTIVE);
        assertNotEquals(CustomerStatus.INACTIVE, CustomerStatus.BLOCKED);
    }

    @Test
    @DisplayName("givenStatus_whenNameMethod_thenReturnCorrectName")
    void givenStatus_whenNameMethod_thenReturnCorrectName() {
        // GIVEN & WHEN & THEN
        assertEquals("ACTIVE", CustomerStatus.ACTIVE.name());
        assertEquals("INACTIVE", CustomerStatus.INACTIVE.name());
        assertEquals("BLOCKED", CustomerStatus.BLOCKED.name());
    }
}
