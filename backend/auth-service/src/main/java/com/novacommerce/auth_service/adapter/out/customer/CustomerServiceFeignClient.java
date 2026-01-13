package com.novacommerce.auth_service.adapter.out.customer;

import com.novacommerce.auth_service.adapter.out.customer.dto.CreateCustomerRequest;
import com.novacommerce.auth_service.adapter.out.customer.dto.CreateCustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Feign Client para comunicación con customer-service.
 * Se usa para crear clientes durante el registro público.
 */
@FeignClient(
    name = "customer-service-registration",
    url = "${app.services.customer-service.url}"
)
public interface CustomerServiceFeignClient {

    /**
     * Crea un nuevo cliente en customer-service mediante endpoint interno.
     * 
     * @param apiKey API Key para autenticación interna
     * @param request datos del cliente a crear
     * @return respuesta con ID del cliente creado
     */
    @PostMapping("/internal/customers")
    CreateCustomerResponse createCustomer(
        @RequestHeader("X-Internal-API-Key") String apiKey,
        @RequestBody CreateCustomerRequest request
    );
}
