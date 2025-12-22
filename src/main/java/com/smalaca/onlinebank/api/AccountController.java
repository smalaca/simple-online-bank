package com.smalaca.onlinebank.api;

import com.smalaca.onlinebank.api.dto.AccountDtos.*;
import com.smalaca.onlinebank.application.AccountService;
import com.smalaca.onlinebank.domain.Account;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> create(@RequestBody CreateAccountRequest req) {
        Account acc = accountService.createAccount(req.customerNumber(), req.currency());
        AccountResponse resp = new AccountResponse(acc.getAccountNumber(), acc.getCustomer().getCustomerNumber(), acc.getCurrency(), acc.getBalance());
        return ResponseEntity.created(URI.create("/api/accounts/" + acc.getAccountNumber())).body(resp);
    }

    @GetMapping("/customer/{customerNumber}")
    public List<AccountResponse> listByCustomer(@PathVariable String customerNumber) {
        return accountService.listCustomerAccounts(customerNumber).stream()
                .map(a -> new AccountResponse(a.getAccountNumber(), a.getCustomer().getCustomerNumber(), a.getCurrency(), a.getBalance()))
                .toList();
    }

    @GetMapping("/{accountNumber}")
    public AccountResponse get(@PathVariable String accountNumber) {
        Account a = accountService.getByAccountNumber(accountNumber);
        return new AccountResponse(a.getAccountNumber(), a.getCustomer().getCustomerNumber(), a.getCurrency(), a.getBalance());
    }

    @PostMapping("/{accountNumber}/deposit")
    public void deposit(@PathVariable String accountNumber, @RequestBody AmountRequest req) {
        accountService.deposit(accountNumber, req.amount());
    }

    @PostMapping("/{accountNumber}/withdraw")
    public void withdraw(@PathVariable String accountNumber, @RequestBody AmountRequest req) {
        accountService.withdraw(accountNumber, req.amount());
    }

    @PostMapping("/transfer")
    public void transfer(@RequestBody TransferRequest req) {
        accountService.transfer(req.sourceAccount(), req.targetAccount(), req.amount());
    }
}
