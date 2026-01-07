package com.novacommerce.product_service.repository.mapper;

import com.novacommerce.product_service.domain.model.Product;
import com.novacommerce.product_service.repository.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductEntityMapper {
    
    Product toDomain(ProductEntity entity);
    
    ProductEntity toEntity(Product domain);
}
