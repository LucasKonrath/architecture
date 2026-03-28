package com.poc.cqrs.command.controller;

import com.poc.cqrs.command.service.ProductCommandService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/commands/products")
public class ProductCommandController {

    private final ProductCommandService commandService;

    public ProductCommandController(ProductCommandService commandService) {
        this.commandService = commandService;
    }

    record CreateProductCommand(
            @NotBlank String name,
            String description,
            @NotNull @Min(0) BigDecimal price,
            @Min(0) int quantity
    ) {}

    record UpdateProductCommand(
            @NotBlank String name,
            String description,
            @NotNull @Min(0) BigDecimal price,
            @Min(0) int quantity
    ) {}

    @PostMapping
    public ResponseEntity<UUID> create(@Valid @RequestBody CreateProductCommand cmd) {
        UUID id = commandService.createProduct(cmd.name(), cmd.description(), cmd.price(), cmd.quantity());
        return ResponseEntity.created(URI.create("/api/queries/products/" + id)).body(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @Valid @RequestBody UpdateProductCommand cmd) {
        commandService.updateProduct(id, cmd.name(), cmd.description(), cmd.price(), cmd.quantity());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        commandService.deleteProduct(id);
    }
}
