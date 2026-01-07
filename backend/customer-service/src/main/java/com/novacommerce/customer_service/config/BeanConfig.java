package com.novacommerce.customer_service.config;

import com.novacommerce.customer_service.application.port.out.CustomerPersistencePort;
import com.novacommerce.customer_service.application.service.CustomerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public CustomerService customerService(CustomerPersistencePort port) {
        return new CustomerService(port);
    }
}
