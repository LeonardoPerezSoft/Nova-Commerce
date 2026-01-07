package com.novacommerce.customer_service.adapter.out.persistence.mapper;

import com.novacommerce.customer_service.adapter.out.persistence.entity.CustomerEntity;
import com.novacommerce.customer_service.domain.model.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerEntityMapper {
    CustomerEntity toEntity(Customer customer);
    Customer toDomain(CustomerEntity entity);
}
