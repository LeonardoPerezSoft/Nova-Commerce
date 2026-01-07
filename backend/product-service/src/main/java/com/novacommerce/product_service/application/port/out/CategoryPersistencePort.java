package com.novacommerce.product_service.application.port.out;

import com.novacommerce.product_service.domain.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CategoryPersistencePort {
    
    Category save(Category category);
    
    Optional<Category> findById(Long id);
    
    Page<Category> findAll(Pageable pageable);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
}
