package com.poc.cqrs.query.controller;

import com.poc.cqrs.query.model.ProductView;
import com.poc.cqrs.query.service.ProductQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/queries/products")
public class ProductQueryController {

    private final ProductQueryService queryService;

    public ProductQueryController(ProductQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public List<ProductView> getAll() {
        return queryService.getAll();
    }

    @GetMapping("/{id}")
    public ProductView getById(@PathVariable UUID id) {
        return queryService.getById(id);
    }

    @GetMapping("/available")
    public List<ProductView> getAvailable() {
        return queryService.getAvailable();
    }

    @GetMapping("/price-range/{range}")
    public List<ProductView> getByPriceRange(@PathVariable String range) {
        return queryService.getByPriceRange(range);
    }

    @GetMapping("/search")
    public List<ProductView> search(@RequestParam String name) {
        return queryService.search(name);
    }
}
