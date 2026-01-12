package com.novacommerce.product_service.adapter.in.web;

import com.novacommerce.product_service.adapter.in.web.dto.PublicProductResponse;
import com.novacommerce.product_service.adapter.in.web.mapper.PublicProductDtoMapper;
import com.novacommerce.product_service.application.port.in.GetPublicProductsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST público para productos.
 * No requiere autenticación.
 */
@RestController
@RequestMapping("/api/public/products")
@RequiredArgsConstructor
@Tag(name = "Public Products", description = "Public product endpoints without authentication")
public class PublicProductController {

    private final GetPublicProductsUseCase getPublicProductsUseCase;
    private final PublicProductDtoMapper publicProductDtoMapper;

    private static final int DEFAULT_HOME_PRODUCTS_LIMIT = 12;

    @GetMapping("/home")
    @Operation(summary = "Get products for home page", 
               description = "Returns a random selection of active products with stock for the home page")
    public ResponseEntity<List<PublicProductResponse>> getHomeProducts() {
        var products = getPublicProductsUseCase.getPublicHomeProducts(DEFAULT_HOME_PRODUCTS_LIMIT);
        var response = publicProductDtoMapper.toPublicResponseList(products);
        return ResponseEntity.ok(response);
    }
}
