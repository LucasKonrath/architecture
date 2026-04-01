package com.example.eventsourcing.dto;

import com.example.eventsourcing.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record EventView(UUID eventId, UUID aggregateId, String type, int version, Instant occurredAt, Object payload) {

    public static EventView from(DomainEvent event) {
        return new EventView(
                event.eventId(),
                event.aggregateId(),
                event.getClass().getSimpleName(),
                event.version(),
                event.occurredAt(),
                event
        );
    }
}
