package com.novacommerce.product_service.repository.mapper;

import com.novacommerce.product_service.domain.model.Category;
import com.novacommerce.product_service.repository.entity.CategoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryEntityMapper {
    
    Category toDomain(CategoryEntity entity);
    
    CategoryEntity toEntity(Category domain);
}
