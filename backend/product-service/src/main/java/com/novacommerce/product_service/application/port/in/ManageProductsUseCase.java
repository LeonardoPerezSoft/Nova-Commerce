package com.novacommerce.product_service.application.port.in;

import com.novacommerce.product_service.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ManageProductsUseCase {
    
    Product createProduct(Product product);
    
    Product updateProduct(Long id, Product product);
    
    void deleteProduct(Long id);
    
    Product getProductById(Long id);
    
    Page<Product> getAllProducts(Pageable pageable);
    
    Page<Product> getProductsByCategoryId(Long categoryId, Pageable pageable);
}
