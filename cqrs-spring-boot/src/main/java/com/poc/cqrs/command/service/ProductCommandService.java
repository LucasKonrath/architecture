package com.poc.cqrs.command.service;

import com.poc.cqrs.command.event.ProductEvent;
import com.poc.cqrs.command.event.ProductEvent.ProductCreated;
import com.poc.cqrs.command.event.ProductEvent.ProductDeleted;
import com.poc.cqrs.command.event.ProductEvent.ProductUpdated;
import com.poc.cqrs.command.model.Product;
import com.poc.cqrs.command.repository.ProductCommandRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ProductCommandService {

    private final ProductCommandRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public ProductCommandService(ProductCommandRepository repository,
                                  ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public UUID createProduct(String name, String description, BigDecimal price, int quantity) {
        var product = new Product(name, description, price, quantity);
        repository.save(product);

        eventPublisher.publishEvent(new ProductCreated(
                product.getId(), product.getName(), product.getDescription(),
                product.getPrice(), product.getQuantity()
        ));

        return product.getId();
    }

    @Transactional
    public void updateProduct(UUID id, String name, String description, BigDecimal price, int quantity) {
        var product = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));

        product.update(name, description, price, quantity);
        repository.save(product);

        eventPublisher.publishEvent(new ProductUpdated(
                product.getId(), product.getName(), product.getDescription(),
                product.getPrice(), product.getQuantity()
        ));
    }

    @Transactional
    public void deleteProduct(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Product not found: " + id);
        }
        repository.deleteById(id);

        eventPublisher.publishEvent(new ProductDeleted(id));
    }
}
