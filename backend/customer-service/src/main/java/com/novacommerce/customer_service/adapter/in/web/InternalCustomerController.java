package com.novacommerce.customer_service.adapter.in.web;

import com.novacommerce.customer_service.application.port.in.ManageCustomersUseCase;
import com.novacommerce.customer_service.domain.model.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

class InternalCustomerResponse {
    public Long id;
    public String firstName;
    public String lastName;
    public String email;
    public String phone;
    public String status;
    public String loyaltyLevel;

    static InternalCustomerResponse from(Customer c) {
        InternalCustomerResponse r = new InternalCustomerResponse();
        r.id = c.getId();
        r.firstName = c.getFirstName();
        r.lastName = c.getLastName();
        r.email = c.getEmail();
        r.phone = c.getPhone();
        r.status = c.getStatus() != null ? c.getStatus().name() : null;
        r.loyaltyLevel = c.getLoyaltyLevel() != null ? c.getLoyaltyLevel().name() : null;
        return r;
    }
}

/**
 * Endpoints internos para consumo entre microservicios.
 * Protegidos por InternalApiKeyFilter y abiertos en SecurityConfig.
 */
@RestController
@RequestMapping("/internal/customers")
public class InternalCustomerController {

    private final ManageCustomersUseCase manageCustomersUseCase;

    public InternalCustomerController(ManageCustomersUseCase manageCustomersUseCase) {
        this.manageCustomersUseCase = manageCustomersUseCase;
    }

    @GetMapping("/{id}")
        public ResponseEntity<InternalCustomerResponse> getByIdInternal(@PathVariable Long id) {
        return manageCustomersUseCase.findById(id)
            .map(c -> ResponseEntity.ok(InternalCustomerResponse.from(c)))
            .orElseGet(() -> ResponseEntity.notFound().build());
        }
}
