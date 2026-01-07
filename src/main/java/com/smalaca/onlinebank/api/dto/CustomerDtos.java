package com.smalaca.onlinebank.api.dto;

import com.smalaca.onlinebank.api.dto.AccountDtos.AccountResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public class CustomerDtos {
    public record CreateCustomerRequest(
            @NotBlank String name,
            @NotBlank String surname,
            @Email String email,
            @Pattern(regexp = "^\\+?[0-9\\s\\-()]{7,20}$") String phoneNumber,
            String address
    ) {}

    public record CustomerResponse(
            String customerNumber,
            String name,
            String surname,
            String email,
            String phoneNumber,
            String address,
            List<AccountResponse> accounts
    ) {}

    public record CustomerDeletionResponse(String status, String message) {}

    public record UpdateCustomerRequest(
            @NotBlank String name,
            @NotBlank String surname,
            @Email String email,
            @Pattern(regexp = "^\\+?[0-9\\s\\-()]{7,20}$") String phoneNumber,
            String address
    ) {}
}
