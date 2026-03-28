package com.poc.cqrs.query.service;

import com.poc.cqrs.query.model.ProductView;
import com.poc.cqrs.query.repository.ProductQueryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProductQueryService {

    private final ProductQueryRepository repository;

    public ProductQueryService(ProductQueryRepository repository) {
        this.repository = repository;
    }

    public ProductView getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
    }

    public List<ProductView> getAll() {
        return repository.findAll();
    }

    public List<ProductView> getAvailable() {
        return repository.findByAvailableTrue();
    }

    public List<ProductView> getByPriceRange(String priceRange) {
        return repository.findByPriceRange(priceRange.toUpperCase());
    }

    public List<ProductView> search(String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }
}
