package com.novacommerce.customer_service.application.port.out;

import com.novacommerce.customer_service.domain.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerPersistencePort {
    Customer save(Customer customer);
    Optional<Customer> findById(Long id);
    List<Customer> findAll();
    void deleteById(Long id);
    boolean existsByEmail(String email);
}
