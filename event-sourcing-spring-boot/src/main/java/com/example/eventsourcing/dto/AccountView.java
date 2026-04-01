package com.example.eventsourcing.dto;

import com.example.eventsourcing.domain.aggregate.BankAccount;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountView(UUID id, String accountHolder, BigDecimal balance, int version) {

    public static AccountView from(BankAccount account) {
        return new AccountView(account.getId(), account.getAccountHolder(), account.getBalance(), account.getVersion());
    }
}
