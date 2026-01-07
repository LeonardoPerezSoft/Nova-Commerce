package com.novacommerce.product_service.repository;

import com.novacommerce.product_service.repository.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    
    Page<ProductEntity> findByCategoryId(Long categoryId, Pageable pageable);
}
