package com.smalaca.onlinebank.api;

import com.smalaca.onlinebank.api.dto.CustomerDtos.*;
import com.smalaca.onlinebank.api.dto.AccountDtos.AccountResponse;
import com.smalaca.onlinebank.application.CustomerService;
import com.smalaca.onlinebank.application.AccountService;
import com.smalaca.onlinebank.domain.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final AccountService accountService;

    public CustomerController(CustomerService customerService, AccountService accountService) {
        this.customerService = customerService;
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@jakarta.validation.Valid @RequestBody CreateCustomerRequest req) {
        Customer saved = customerService.addCustomer(req.name(), req.surname(), req.email(), req.phoneNumber(), req.address());
        CustomerResponse resp = toResponse(saved);
        return ResponseEntity.created(URI.create("/api/customers/" + saved.getCustomerNumber())).body(resp);
    }

    @GetMapping
    public List<CustomerResponse> all() {
        return customerService.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{number}")
    public CustomerResponse get(@PathVariable String number) {
        Customer c = customerService.getByNumber(number);
        return toResponse(c);
    }

    private CustomerResponse toResponse(Customer c) {
        List<AccountResponse> accounts = accountService.listCustomerAccounts(c.getCustomerNumber()).stream()
                .map(a -> new AccountResponse(a.getAccountNumber(), a.getCustomer().getCustomerNumber(), a.getCurrency(), a.getBalance()))
                .toList();
        return new CustomerResponse(
                c.getCustomerNumber(),
                c.getName(),
                c.getSurname(),
                c.getEmail(),
                c.getPhoneNumber(),
                c.getAddress(),
                accounts);
    }

    @DeleteMapping("/{customerNumber}")
    public ResponseEntity<CustomerDeletionResponse> delete(@PathVariable String customerNumber) {
        CustomerService.CustomerDeletionResult result = customerService.deleteCustomer(customerNumber);

        if (result.success()) {
            return ResponseEntity.ok(new CustomerDeletionResponse("success", "Customer " + result.customerNumber() + " removed successfully"));
        } else {
            return ResponseEntity.ok(new CustomerDeletionResponse("error", "Customer " + result.customerNumber() + " has accounts and cannot be removed"));
        }
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Void> handleNotFound() {
        return ResponseEntity.notFound().build();
    }
}
