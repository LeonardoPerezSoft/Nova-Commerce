package com.novacommerce.order_service.adapter.in.web.dto;

import com.novacommerce.order_service.domain.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar estado de orden.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {
    
    @NotNull(message = "Status is required")
    private OrderStatus status;
}
