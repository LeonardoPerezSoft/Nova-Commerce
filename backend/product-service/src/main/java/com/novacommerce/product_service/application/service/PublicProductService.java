package com.novacommerce.product_service.application.service;

import com.novacommerce.product_service.application.port.in.GetPublicProductsUseCase;
import com.novacommerce.product_service.application.port.out.ProductPersistencePort;
import com.novacommerce.product_service.domain.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para obtener productos públicos para el home.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PublicProductService implements GetPublicProductsUseCase {

    private final ProductPersistencePort productPersistencePort;

    @Override
    @Transactional(readOnly = true)
    public List<Product> getPublicHomeProducts(int limit) {
        log.info("Fetching {} public products for home in random order", limit);
        return productPersistencePort.findActiveProductsWithStockRandomOrder(limit);
    }
}
