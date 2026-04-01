package com.example.eventsourcing.domain.event;

import java.time.Instant;
import java.util.UUID;

public sealed interface DomainEvent permits
        AccountCreatedEvent,
        MoneyDepositedEvent,
        MoneyWithdrawnEvent {

    UUID eventId();
    UUID aggregateId();
    Instant occurredAt();
    int version();
}
