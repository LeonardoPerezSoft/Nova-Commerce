package com.novacommerce.product_service.repository;

import com.novacommerce.product_service.repository.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    
    Page<ProductEntity> findByCategoryId(Long categoryId, Pageable pageable);
    
    /**
     * Encuentra productos activos con stock disponible en orden aleatorio.
     *
     * @param status estado del producto (ej. "ACTIVE")
     * @param minStock cantidad mínima de stock
     * @param limit número máximo de productos a retornar
     * @return lista de productos que cumplen los criterios
     */
    @Query(value = "SELECT * FROM products p " +
                   "WHERE p.status = :status " +
                   "AND p.stock_quantity > :minStock " +
                   "ORDER BY RANDOM() " +
                   "LIMIT :limit", 
           nativeQuery = true)
    List<ProductEntity> findActiveProductsWithStockRandomOrder(
            @Param("status") String status,
            @Param("minStock") int minStock,
            @Param("limit") int limit
    );
}
