package com.novacommerce.product_service.adapter.in.web.mapper;

import com.novacommerce.product_service.adapter.in.web.dto.ProductRequest;
import com.novacommerce.product_service.adapter.in.web.dto.ProductResponse;
import com.novacommerce.product_service.domain.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductDtoMapper {
    
    Product toDomain(ProductRequest request);
    
    ProductResponse toResponse(Product domain);
}
