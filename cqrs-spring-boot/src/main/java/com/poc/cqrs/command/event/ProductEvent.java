package com.poc.cqrs.command.event;

import java.math.BigDecimal;
import java.util.UUID;

public sealed interface ProductEvent {

    UUID productId();

    record ProductCreated(
            UUID productId,
            String name,
            String description,
            BigDecimal price,
            int quantity
    ) implements ProductEvent {}

    record ProductUpdated(
            UUID productId,
            String name,
            String description,
            BigDecimal price,
            int quantity
    ) implements ProductEvent {}

    record ProductDeleted(
            UUID productId
    ) implements ProductEvent {}
}
