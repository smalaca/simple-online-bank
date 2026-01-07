package com.smalaca.onlinebank.api.dto;

import com.smalaca.onlinebank.domain.Currency;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class AccountDtos {
    public record CreateAccountRequest(String customerNumber, Currency currency) {}
    public record AccountResponse(String accountNumber, String customerNumber, Currency currency, BigDecimal balance) {}
    public record AmountRequest(@Positive BigDecimal amount) {}
    public record TransferRequest(String sourceAccount, String targetAccount, @Positive BigDecimal amount) {}
    public record AccountDeletionResponse(String status, String message) {}
}
