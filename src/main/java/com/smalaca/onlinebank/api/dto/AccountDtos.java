package com.smalaca.onlinebank.api.dto;

import com.smalaca.onlinebank.domain.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class AccountDtos {
    public record CreateAccountRequest(String customerNumber, Currency currency) {}
    public record AccountResponse(String accountNumber, String customerNumber, Currency currency, BigDecimal balance) {}
    public record AmountRequest(@Positive BigDecimal amount) {}
    public record TransferRequest(String sourceAccount, String targetAccount, @Positive BigDecimal amount) {}
    public record AccountDeletionResponse(String status, String message) {}
    public record ValidationError(String field, String message) {}
    public record TransactionResponse(Instant date, String type, BigDecimal amount, String from, String to) {}
    public record OfferPolicyResponse(String offerId, List<PolicyResponse> policies) {}
    public record PolicyResponse(String name, String description) {}

    public record CreditOfferRequest(
            @Schema(description = "client identifier", type = "number")
            String firstName,
            String lastName,
            BigDecimal amount,
            @Schema(description = "client salary", required = true)
            BigDecimal salary,
            BigDecimal currentCreditsAmount
    ) {
        public boolean hasCustomerName() {
            String fullName = String.join("", firstName, lastName);
            return !fullName.trim().isEmpty() && fullName.matches("[a-zA-Z\\s-]+");
        }
    }
    public record CreditOfferResponse(String status, BigDecimal proposedMaxAmount, String reason, String contactDetails) {}
}
