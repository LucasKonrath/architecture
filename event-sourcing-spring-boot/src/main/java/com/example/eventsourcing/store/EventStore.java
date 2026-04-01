package com.example.eventsourcing.store;

import com.example.eventsourcing.domain.event.DomainEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory event store. In production this would be backed by a database
 * (e.g. EventStoreDB, PostgreSQL with an events table, etc.).
 *
 * Key characteristics:
 * - Append-only: events are never updated or deleted.
 * - Events are stored per aggregate (stream).
 * - Optimistic concurrency via expected version checks.
 */
@Component
public class EventStore {

    private final Map<UUID, List<DomainEvent>> streams = new ConcurrentHashMap<>();

    public void saveEvents(UUID aggregateId, List<DomainEvent> newEvents, int expectedVersion) {
        var existingEvents = streams.computeIfAbsent(aggregateId, k -> new ArrayList<>());

        synchronized (existingEvents) {
            int currentVersion = existingEvents.isEmpty() ? 0 : existingEvents.getLast().version();

            if (currentVersion != expectedVersion) {
                throw new IllegalStateException(
                        "Optimistic concurrency conflict. Expected version %d but found %d for aggregate %s"
                                .formatted(expectedVersion, currentVersion, aggregateId));
            }

            existingEvents.addAll(newEvents);
        }
    }

    public List<DomainEvent> getEvents(UUID aggregateId) {
        return Collections.unmodifiableList(
                streams.getOrDefault(aggregateId, List.of()));
    }

    public List<DomainEvent> getAllEvents() {
        return streams.values().stream()
                .flatMap(List::stream)
                .sorted((a, b) -> a.occurredAt().compareTo(b.occurredAt()))
                .toList();
    }
}
