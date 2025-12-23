package com.smalaca.onlinebank.api.dto;

import com.smalaca.onlinebank.api.dto.AccountDtos.AccountResponse;
import java.util.List;

public class CustomerDtos {
    public record CreateCustomerRequest(String customerNumber, String name) {}
    public record CustomerResponse(String customerNumber, String name, List<AccountResponse> accounts) {}
}
