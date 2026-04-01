package com.example.eventsourcing.domain.aggregate;

import com.example.eventsourcing.domain.event.AccountCreatedEvent;
import com.example.eventsourcing.domain.event.DomainEvent;
import com.example.eventsourcing.domain.event.MoneyDepositedEvent;
import com.example.eventsourcing.domain.event.MoneyWithdrawnEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * The BankAccount aggregate. State is never stored directly — it is always
 * rebuilt by replaying events from the event store.
 */
public class BankAccount {

    private UUID id;
    private String accountHolder;
    private BigDecimal balance;
    private int version;

    private final List<DomainEvent> uncommittedChanges = new ArrayList<>();

    private BankAccount() {
        this.balance = BigDecimal.ZERO;
        this.version = 0;
    }

    // --- Factory (command) ---

    public static BankAccount create(String accountHolder, BigDecimal initialBalance) {
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        var account = new BankAccount();
        var event = AccountCreatedEvent.create(UUID.randomUUID(), accountHolder, initialBalance, account.nextVersion());
        account.apply(event);
        account.uncommittedChanges.add(event);
        return account;
    }

    // --- Commands ---

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        var event = MoneyDepositedEvent.create(this.id, amount, nextVersion());
        apply(event);
        uncommittedChanges.add(event);
    }

    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds. Current balance: " + balance);
        }
        var event = MoneyWithdrawnEvent.create(this.id, amount, nextVersion());
        apply(event);
        uncommittedChanges.add(event);
    }

    // --- Event application (state mutation) ---

    public void apply(DomainEvent event) {
        switch (event) {
            case AccountCreatedEvent e -> {
                this.id = e.aggregateId();
                this.accountHolder = e.accountHolder();
                this.balance = e.initialBalance();
                this.version = e.version();
            }
            case MoneyDepositedEvent e -> {
                this.balance = this.balance.add(e.amount());
                this.version = e.version();
            }
            case MoneyWithdrawnEvent e -> {
                this.balance = this.balance.subtract(e.amount());
                this.version = e.version();
            }
        }
    }

    // --- Reconstitution from event history ---

    public static BankAccount reconstitute(List<DomainEvent> eventHistory) {
        var account = new BankAccount();
        for (DomainEvent event : eventHistory) {
            account.apply(event);
        }
        return account;
    }

    // --- Helpers ---

    private int nextVersion() {
        return this.version + 1;
    }

    public List<DomainEvent> getUncommittedChanges() {
        return Collections.unmodifiableList(uncommittedChanges);
    }

    public void markChangesAsCommitted() {
        uncommittedChanges.clear();
    }

    public UUID getId() { return id; }
    public String getAccountHolder() { return accountHolder; }
    public BigDecimal getBalance() { return balance; }
    public int getVersion() { return version; }
}
