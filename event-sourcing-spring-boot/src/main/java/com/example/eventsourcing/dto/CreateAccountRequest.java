package com.example.eventsourcing.dto;

import java.math.BigDecimal;

public record CreateAccountRequest(String accountHolder, BigDecimal initialBalance) {}
