package com.novacommerce.order_service.adapter.out.persistence;

import com.novacommerce.order_service.application.port.out.OrderPersistencePort;
import com.novacommerce.order_service.domain.model.Order;
import com.novacommerce.order_service.repository.OrderRepository;
import com.novacommerce.order_service.repository.entity.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia para órdenes.
 * Implementa el puerto de salida usando JPA.
 */
@Component
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements OrderPersistencePort {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public Order save(Order order) {
        OrderEntity entity = orderMapper.toEntity(order);
        OrderEntity saved = orderRepository.save(entity);
        return orderMapper.toDomain(saved);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::toDomain);
    }

    @Override
    public List<Order> findByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }
}
