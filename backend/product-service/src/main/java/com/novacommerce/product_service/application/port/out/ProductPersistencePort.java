package com.novacommerce.product_service.application.port.out;

import com.novacommerce.product_service.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductPersistencePort {
    
    Product save(Product product);
    
    Optional<Product> findById(Long id);
    
    Page<Product> findAll(Pageable pageable);
    
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
    
    /**
     * Encuentra productos activos con stock disponible en orden aleatorio.
     *
     * @param limit número máximo de productos a retornar
     * @return lista de productos activos con stock
     */
    List<Product> findActiveProductsWithStockRandomOrder(int limit);
}
