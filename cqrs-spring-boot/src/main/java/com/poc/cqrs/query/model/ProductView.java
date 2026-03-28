package com.poc.cqrs.query.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products_read")
public class ProductView {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private boolean available;

    @Column(nullable = false)
    private String priceRange;

    protected ProductView() {}

    public ProductView(UUID id, String name, String description, BigDecimal price, int quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.available = quantity > 0;
        this.priceRange = categorizePriceRange(price);
    }

    public void update(String name, String description, BigDecimal price, int quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.available = quantity > 0;
        this.priceRange = categorizePriceRange(price);
    }

    private static String categorizePriceRange(BigDecimal price) {
        if (price.compareTo(new BigDecimal("50")) < 0) return "BUDGET";
        if (price.compareTo(new BigDecimal("200")) < 0) return "MID_RANGE";
        return "PREMIUM";
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public boolean isAvailable() { return available; }
    public String getPriceRange() { return priceRange; }
}
