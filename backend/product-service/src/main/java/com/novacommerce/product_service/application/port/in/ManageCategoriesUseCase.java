package com.novacommerce.product_service.application.port.in;

import com.novacommerce.product_service.domain.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ManageCategoriesUseCase {
    
    Category createCategory(Category category);
    
    Category updateCategory(Long id, Category category);
    
    void deleteCategory(Long id);
    
    Category getCategoryById(Long id);
    
    Page<Category> getAllCategories(Pageable pageable);
}
