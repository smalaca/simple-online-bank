package com.smalaca.onlinebank.api.dto;

public class CustomerDtos {
    public record CreateCustomerRequest(String customerNumber, String name) {}
    public record CustomerResponse(String customerNumber, String name) {}
}
