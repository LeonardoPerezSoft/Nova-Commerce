package com.novacommerce.order_service.repository;

import com.novacommerce.order_service.repository.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
}
