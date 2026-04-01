package com.example.eventsourcing.controller;

import com.example.eventsourcing.domain.aggregate.BankAccount;
import com.example.eventsourcing.dto.AccountView;
import com.example.eventsourcing.dto.CreateAccountRequest;
import com.example.eventsourcing.dto.EventView;
import com.example.eventsourcing.dto.MoneyRequest;
import com.example.eventsourcing.store.EventStore;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class BankAccountController {

    private final EventStore eventStore;

    public BankAccountController(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountView createAccount(@RequestBody CreateAccountRequest request) {
        var account = BankAccount.create(request.accountHolder(), request.initialBalance());
        eventStore.saveEvents(account.getId(), account.getUncommittedChanges(), 0);
        account.markChangesAsCommitted();
        return AccountView.from(account);
    }

    @PostMapping("/{id}/deposit")
    public AccountView deposit(@PathVariable UUID id, @RequestBody MoneyRequest request) {
        var account = loadAggregate(id);
        int previousVersion = account.getVersion();
        account.deposit(request.amount());
        eventStore.saveEvents(id, account.getUncommittedChanges(), previousVersion);
        account.markChangesAsCommitted();
        return AccountView.from(account);
    }

    @PostMapping("/{id}/withdraw")
    public AccountView withdraw(@PathVariable UUID id, @RequestBody MoneyRequest request) {
        var account = loadAggregate(id);
        int previousVersion = account.getVersion();
        account.withdraw(request.amount());
        eventStore.saveEvents(id, account.getUncommittedChanges(), previousVersion);
        account.markChangesAsCommitted();
        return AccountView.from(account);
    }

    @GetMapping("/{id}")
    public AccountView getAccount(@PathVariable UUID id) {
        return AccountView.from(loadAggregate(id));
    }

    @GetMapping("/{id}/events")
    public List<EventView> getAccountEvents(@PathVariable UUID id) {
        var events = eventStore.getEvents(id);
        if (events.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }
        return events.stream().map(EventView::from).toList();
    }

    @GetMapping("/events")
    public List<EventView> getAllEvents() {
        return eventStore.getAllEvents().stream().map(EventView::from).toList();
    }

    private BankAccount loadAggregate(UUID id) {
        var events = eventStore.getEvents(id);
        if (events.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }
        return BankAccount.reconstitute(events);
    }
}
