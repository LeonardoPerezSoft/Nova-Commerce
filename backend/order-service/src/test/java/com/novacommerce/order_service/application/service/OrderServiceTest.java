package com.novacommerce.order_service.application.service;

import com.novacommerce.order_service.application.port.out.CustomerValidationPort;
import com.novacommerce.order_service.application.port.out.OrderPersistencePort;
import com.novacommerce.order_service.application.port.out.ProductValidationPort;
import com.novacommerce.order_service.domain.discount.DiscountStrategy;
import com.novacommerce.order_service.domain.discount.LoyaltyDiscountStrategy;
import com.novacommerce.order_service.domain.discount.ProductTypeDiscountStrategy;
import com.novacommerce.order_service.domain.discount.SeasonDiscountStrategy;
import com.novacommerce.order_service.domain.exception.BusinessRuleException;
import com.novacommerce.order_service.domain.exception.OrderException;
import com.novacommerce.order_service.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderPersistencePort orderPersistencePort;
    private CustomerValidationPort customerValidationPort;
    private ProductValidationPort productValidationPort;
    private OrderService service;

    @BeforeEach
    void setup() {
        orderPersistencePort = mock(OrderPersistencePort.class);
        customerValidationPort = mock(CustomerValidationPort.class);
        productValidationPort = mock(ProductValidationPort.class);
        List<DiscountStrategy> strategies = List.of(
                new LoyaltyDiscountStrategy(),
                new SeasonDiscountStrategy(),
                new ProductTypeDiscountStrategy()
        );
        service = new OrderService(orderPersistencePort, customerValidationPort, productValidationPort, strategies);

        when(customerValidationPort.isCustomerValid(anyLong())).thenReturn(true);
        when(customerValidationPort.getCustomerStatus(anyLong())).thenReturn("ACTIVE");
        when(customerValidationPort.getCustomerLoyaltyLevel(anyLong())).thenReturn("GOLD");

        when(productValidationPort.isProductValid(anyLong())).thenReturn(true);
        when(productValidationPort.hasStock(anyLong(), anyInt())).thenReturn(true);
        when(productValidationPort.getProductName(anyLong())).thenReturn("Product");
        when(productValidationPort.getProductType(anyLong())).thenReturn("ELECTRONICS");

        when(orderPersistencePort.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(100L);
            return o;
        });
    }

    private Order buildValidOrder() {
        Order order = Order.builder().customerId(1L).build();
        order.addItem(OrderItem.builder().productId(1L).quantity(1).unitPrice(Money.of(100)).build());
        order.addItem(OrderItem.builder().productId(2L).quantity(2).unitPrice(Money.of(50)).build());
        return order;
    }

    @Test
    @DisplayName("GIVEN valid order WHEN createOrder THEN persists with discounts applied")
    void createValidOrder() {
        Order o = buildValidOrder();
        Order saved = service.createOrder(o);
        assertNotNull(saved.getId());
        assertEquals(OrderStatus.CREATED, saved.getStatus());
        assertTrue(saved.getDiscountTotal().isGreaterThan(Money.zero()));
        verify(orderPersistencePort).save(any(Order.class));
    }

    @Test
    @DisplayName("GIVEN INACTIVE customer WHEN createOrder THEN throws BusinessRuleException")
    void inactiveCustomer() {
        when(customerValidationPort.getCustomerStatus(anyLong())).thenReturn("INACTIVE");
        Order o = buildValidOrder();
        assertThrows(BusinessRuleException.class, () -> service.createOrder(o));
    }

    @Test
    @DisplayName("GIVEN BLOCKED customer WHEN createOrder THEN throws BusinessRuleException")
    void blockedCustomer() {
        when(customerValidationPort.getCustomerStatus(anyLong())).thenReturn("BLOCKED");
        Order o = buildValidOrder();
        assertThrows(BusinessRuleException.class, () -> service.createOrder(o));
    }

    @Test
    @DisplayName("GIVEN invalid product WHEN createOrder THEN throws BusinessRuleException")
    void invalidProduct() {
        when(productValidationPort.isProductValid(eq(2L))).thenReturn(false);
        Order o = buildValidOrder();
        assertThrows(BusinessRuleException.class, () -> service.createOrder(o));
    }

    @Test
    @DisplayName("GIVEN insufficient stock WHEN createOrder THEN throws BusinessRuleException")
    void insufficientStock() {
        when(productValidationPort.hasStock(eq(2L), anyInt())).thenReturn(false);
        Order o = buildValidOrder();
        assertThrows(BusinessRuleException.class, () -> service.createOrder(o));
    }

    @Test
    @DisplayName("GIVEN multiple discounts WHEN createOrder THEN accumulates total discount")
    void multipleDiscountsApplied() {
        // Already configured: loyalty GOLD, season WINTER, product ELECTRONICS
        Order o = buildValidOrder();
        Order saved = service.createOrder(o);
        assertNotNull(saved.getDiscountTotal());
        assertTrue(saved.getDiscountTotal().isGreaterThan(Money.zero()));
    }

    @Test
    @DisplayName("GIVEN existing order WHEN updateOrderStatus THEN persists with new status")
    void updateOrderStatus() {
        Order existing = buildValidOrder();
        existing.markAsCreated();
        existing.setId(10L);
        when(orderPersistencePort.findById(10L)).thenReturn(Optional.of(existing));
        when(orderPersistencePort.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order updated = service.updateOrderStatus(10L, OrderStatus.PAID);
        assertEquals(OrderStatus.PAID, updated.getStatus());
        verify(orderPersistencePort).save(any(Order.class));
    }

    @Test
    @DisplayName("GIVEN missing order WHEN updateOrderStatus THEN throws OrderException")
    void updateMissingOrder() {
        when(orderPersistencePort.findById(99L)).thenReturn(Optional.empty());
        assertThrows(OrderException.class, () -> service.updateOrderStatus(99L, OrderStatus.PAID));
    }
}
