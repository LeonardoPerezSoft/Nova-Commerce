package com.novacommerce.customer_service.adapter.in.web.mapper;

import com.novacommerce.customer_service.adapter.in.web.dto.CustomerDto;
import com.novacommerce.customer_service.domain.model.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDto toDto(Customer customer);
    Customer toDomain(CustomerDto dto);
}
