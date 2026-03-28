package com.poc.cqrs.command.repository;

import com.poc.cqrs.command.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductCommandRepository extends JpaRepository<Product, UUID> {
}
