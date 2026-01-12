package com.novacommerce.product_service.application.port.in;

import com.novacommerce.product_service.domain.model.Product;

import java.util.List;

/**
 * Puerto de entrada para obtener productos públicos para el home.
 */
public interface GetPublicProductsUseCase {
    
    /**
     * Obtiene una lista de productos activos con stock disponible
     * en orden aleatorio para mostrar en el home.
     *
     * @param limit número máximo de productos a retornar
     * @return lista de productos en orden aleatorio
     */
    List<Product> getPublicHomeProducts(int limit);
}
