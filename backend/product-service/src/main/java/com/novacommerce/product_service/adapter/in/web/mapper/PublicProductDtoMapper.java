package com.novacommerce.product_service.adapter.in.web.mapper;

import com.novacommerce.product_service.adapter.in.web.dto.PublicProductResponse;
import com.novacommerce.product_service.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper para convertir Product a PublicProductResponse.
 */
@Mapper(componentModel = "spring")
public interface PublicProductDtoMapper {
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "productType", source = "productType")
    PublicProductResponse toPublicResponse(Product product);
    
    List<PublicProductResponse> toPublicResponseList(List<Product> products);
}
