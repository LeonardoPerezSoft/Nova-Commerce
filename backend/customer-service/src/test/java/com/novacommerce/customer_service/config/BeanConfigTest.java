package com.novacommerce.customer_service.config;

import com.novacommerce.customer_service.application.port.out.CustomerPersistencePort;
import com.novacommerce.customer_service.application.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BeanConfig Tests")
class BeanConfigTest {

    @Mock
    private CustomerPersistencePort persistencePort;

    @Test
    @DisplayName("givenPersistencePort_whenCustomerServiceBean_thenReturnService")
    void givenPersistencePort_whenCustomerServiceBean_thenReturnService() {
        // GIVEN
        BeanConfig config = new BeanConfig();

        // WHEN
        CustomerService service = config.customerService(persistencePort);

        // THEN
        assertNotNull(service);
    }

    @Test
    @DisplayName("givenConfig_whenCreateMultipleBeans_thenDifferentInstances")
    void givenConfig_whenCreateMultipleBeans_thenDifferentInstances() {
        // GIVEN
        BeanConfig config = new BeanConfig();

        // WHEN
        CustomerService service1 = config.customerService(persistencePort);
        CustomerService service2 = config.customerService(persistencePort);

        // THEN
        assertNotNull(service1);
        assertNotNull(service2);
        assertNotSame(service1, service2);
    }
}
