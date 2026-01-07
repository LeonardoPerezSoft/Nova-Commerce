package com.novacommerce.product_service.adapter.in.web.mapper;

import com.novacommerce.product_service.adapter.in.web.dto.CategoryRequest;
import com.novacommerce.product_service.adapter.in.web.dto.CategoryResponse;
import com.novacommerce.product_service.domain.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryDtoMapper {
    
    Category toDomain(CategoryRequest request);
    
    CategoryResponse toResponse(Category domain);
}
