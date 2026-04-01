package com.example.eventsourcing.domain.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountCreatedEvent(
        UUID eventId,
        UUID aggregateId,
        String accountHolder,
        BigDecimal initialBalance,
        Instant occurredAt,
        int version
) implements DomainEvent {

    public static AccountCreatedEvent create(UUID aggregateId, String accountHolder, BigDecimal initialBalance, int version) {
        return new AccountCreatedEvent(UUID.randomUUID(), aggregateId, accountHolder, initialBalance, Instant.now(), version);
    }
}
