package com.novacommerce.product_service.adapter.out.persistence;

import com.novacommerce.product_service.application.port.out.ProductPersistencePort;
import com.novacommerce.product_service.domain.model.Product;
import com.novacommerce.product_service.repository.ProductRepository;
import com.novacommerce.product_service.repository.mapper.ProductEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductPersistencePort {

    private final ProductRepository productRepository;
    private final ProductEntityMapper productEntityMapper;

    @Override
    public Product save(Product product) {
        var entity = productEntityMapper.toEntity(product);
        var savedEntity = productRepository.save(entity);
        return productEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id)
                .map(productEntityMapper::toDomain);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productEntityMapper::toDomain);
    }

    @Override
    public Page<Product> findByCategoryId(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable)
                .map(productEntityMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    @Override
    public List<Product> findActiveProductsWithStockRandomOrder(int limit) {
        return productRepository.findActiveProductsWithStockRandomOrder("ACTIVE", 0, limit)
                .stream()
                .map(productEntityMapper::toDomain)
                .collect(Collectors.toList());
    }
}
