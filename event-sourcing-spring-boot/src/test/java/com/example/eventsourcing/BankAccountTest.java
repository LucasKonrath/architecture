package com.example.eventsourcing;

import com.example.eventsourcing.domain.aggregate.BankAccount;
import com.example.eventsourcing.domain.event.AccountCreatedEvent;
import com.example.eventsourcing.domain.event.MoneyDepositedEvent;
import com.example.eventsourcing.domain.event.MoneyWithdrawnEvent;
import com.example.eventsourcing.store.EventStore;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BankAccountTest {

    @Test
    void shouldCreateAccountWithInitialBalance() {
        var account = BankAccount.create("Alice", new BigDecimal("100.00"));

        assertEquals("Alice", account.getAccountHolder());
        assertEquals(new BigDecimal("100.00"), account.getBalance());
        assertEquals(1, account.getVersion());
        assertEquals(1, account.getUncommittedChanges().size());
        assertInstanceOf(AccountCreatedEvent.class, account.getUncommittedChanges().getFirst());
    }

    @Test
    void shouldDepositMoney() {
        var account = BankAccount.create("Alice", new BigDecimal("100.00"));
        account.deposit(new BigDecimal("50.00"));

        assertEquals(new BigDecimal("150.00"), account.getBalance());
        assertEquals(2, account.getVersion());
        assertEquals(2, account.getUncommittedChanges().size());
    }

    @Test
    void shouldWithdrawMoney() {
        var account = BankAccount.create("Alice", new BigDecimal("100.00"));
        account.withdraw(new BigDecimal("30.00"));

        assertEquals(new BigDecimal("70.00"), account.getBalance());
        assertEquals(2, account.getVersion());
    }

    @Test
    void shouldRejectWithdrawalExceedingBalance() {
        var account = BankAccount.create("Alice", new BigDecimal("100.00"));

        assertThrows(IllegalStateException.class, () ->
                account.withdraw(new BigDecimal("200.00")));
    }

    @Test
    void shouldReconstituteFromEvents() {
        var account = BankAccount.create("Bob", new BigDecimal("200.00"));
        account.deposit(new BigDecimal("50.00"));
        account.withdraw(new BigDecimal("30.00"));

        // Simulate saving and loading from event store
        var reconstituted = BankAccount.reconstitute(account.getUncommittedChanges());

        assertEquals(account.getId(), reconstituted.getId());
        assertEquals("Bob", reconstituted.getAccountHolder());
        assertEquals(new BigDecimal("220.00"), reconstituted.getBalance());
        assertEquals(3, reconstituted.getVersion());
    }

    @Test
    void shouldDetectOptimisticConcurrencyConflict() {
        var store = new EventStore();
        var account = BankAccount.create("Carol", new BigDecimal("500.00"));
        store.saveEvents(account.getId(), account.getUncommittedChanges(), 0);
        account.markChangesAsCommitted();

        // Simulate two concurrent reads
        var read1 = BankAccount.reconstitute(store.getEvents(account.getId()));
        var read2 = BankAccount.reconstitute(store.getEvents(account.getId()));

        // First write succeeds
        read1.deposit(new BigDecimal("100.00"));
        store.saveEvents(read1.getId(), read1.getUncommittedChanges(), 1);

        // Second write should fail — stale version
        read2.withdraw(new BigDecimal("50.00"));
        assertThrows(IllegalStateException.class, () ->
                store.saveEvents(read2.getId(), read2.getUncommittedChanges(), 1));
    }

    @Test
    void shouldTrackEventTypes() {
        var account = BankAccount.create("Dave", new BigDecimal("100.00"));
        account.deposit(new BigDecimal("25.00"));
        account.withdraw(new BigDecimal("10.00"));

        var events = account.getUncommittedChanges();
        assertInstanceOf(AccountCreatedEvent.class, events.get(0));
        assertInstanceOf(MoneyDepositedEvent.class, events.get(1));
        assertInstanceOf(MoneyWithdrawnEvent.class, events.get(2));
    }
}
