package com.poc.cqrs.query.repository;

import com.poc.cqrs.query.model.ProductView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductQueryRepository extends JpaRepository<ProductView, UUID> {

    List<ProductView> findByAvailableTrue();

    List<ProductView> findByPriceRange(String priceRange);

    List<ProductView> findByNameContainingIgnoreCase(String name);
}
