package com.example.eventsourcing.domain.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record MoneyWithdrawnEvent(
        UUID eventId,
        UUID aggregateId,
        BigDecimal amount,
        Instant occurredAt,
        int version
) implements DomainEvent {

    public static MoneyWithdrawnEvent create(UUID aggregateId, BigDecimal amount, int version) {
        return new MoneyWithdrawnEvent(UUID.randomUUID(), aggregateId, amount, Instant.now(), version);
    }
}
