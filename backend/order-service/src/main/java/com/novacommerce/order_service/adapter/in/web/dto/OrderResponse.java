package com.novacommerce.order_service.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respuesta de orden.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long customerId;
    private String status;
    private BigDecimal totalBeforeDiscount;
    private BigDecimal discountTotal;
    private BigDecimal totalAfterDiscount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponse> items;
}
