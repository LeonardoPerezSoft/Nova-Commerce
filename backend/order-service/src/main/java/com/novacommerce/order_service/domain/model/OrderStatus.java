package com.novacommerce.order_service.domain.model;

public enum OrderStatus {
    CREATED,
    PAID,
    CANCELLED,
    SHIPPED,
    COMPLETED;

    public boolean canTransitionTo(OrderStatus newStatus) {
        return switch (this) {
            case CREATED -> newStatus == PAID || newStatus == CANCELLED;
            case PAID -> newStatus == SHIPPED || newStatus == CANCELLED;
            case SHIPPED -> newStatus == COMPLETED;
            case CANCELLED, COMPLETED -> false;
        };
    }

    public boolean isFinal() {
        return this == CANCELLED || this == COMPLETED;
    }
}
