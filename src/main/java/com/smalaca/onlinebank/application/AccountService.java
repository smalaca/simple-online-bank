package com.smalaca.onlinebank.application;

import com.smalaca.onlinebank.domain.Account;
import com.smalaca.onlinebank.domain.Currency;
import com.smalaca.onlinebank.domain.Customer;
import com.smalaca.onlinebank.domain.Transaction;
import com.smalaca.onlinebank.domain.TransactionType;
import com.smalaca.onlinebank.repository.AccountRepository;
import com.smalaca.onlinebank.repository.CustomerRepository;
import com.smalaca.onlinebank.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
public class AccountService {
    private final AccountRepository accounts;
    private final CustomerRepository customers;
    private final TransactionRepository transactions;
    private final RatesEngine ratesEngine;

    public AccountService(AccountRepository accounts, CustomerRepository customers, TransactionRepository transactions) {
        this.accounts = accounts;
        this.customers = customers;
        this.transactions = transactions;
        this.ratesEngine = new RatesEngine();
    }

    public Account createAccount(String customerNumber, Currency currency) {
        Customer customer = customers.findByCustomerNumber(customerNumber)
                .orElseThrow(() -> new NoSuchElementException("Customer not found: " + customerNumber));
        String accountNumber = generateAccountNumber();
        Account account = new Account(accountNumber, customer, currency);
        return accounts.save(account);
    }

    @Transactional(readOnly = true)
    public List<Account> listCustomerAccounts(String customerNumber) {
        Customer customer = customers.findByCustomerNumber(customerNumber)
                .orElseThrow(() -> new NoSuchElementException("Customer not found: " + customerNumber));
        return accounts.findByCustomer(customer);
    }

    @Transactional(readOnly = true)
    public Account getByAccountNumber(String accountNumber) {
        return accounts.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NoSuchElementException("Account not found: " + accountNumber));
    }

    public void deposit(String accountNumber, BigDecimal amount) {
        if (amount.signum() <= 0) throw new IllegalArgumentException("Amount must be positive");
        Account acc = getByAccountNumber(accountNumber);
        acc.credit(amount.setScale(4, RoundingMode.HALF_UP));
        transactions.save(new Transaction(acc, null, TransactionType.DEPOSIT, amount));
    }

    public void withdraw(String accountNumber, BigDecimal amount) {
        if (amount.signum() <= 0) throw new IllegalArgumentException("Amount must be positive");
        Account acc = getByAccountNumber(accountNumber);
        if (acc.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        acc.debit(amount.setScale(4, RoundingMode.HALF_UP));
        transactions.save(new Transaction(acc, null, TransactionType.WITHDRAWAL, amount));
    }

    public void transfer(String sourceAccount, String targetAccount, BigDecimal amount) {
        if (amount.signum() <= 0) throw new IllegalArgumentException("Amount must be positive");
        Account src = getByAccountNumber(sourceAccount);
        Account tgt = getByAccountNumber(targetAccount);
        if (src.getBalance().compareTo(amount) < 0) throw new IllegalStateException("Insufficient funds");

        // Debit source
        src.debit(amount.setScale(4, RoundingMode.HALF_UP));
        // Convert and credit target
        BigDecimal converted = ratesEngine.convert(amount, src.getCurrency(), tgt.getCurrency());
        tgt.credit(converted);

        transactions.save(new Transaction(src, tgt, TransactionType.TRANSFER, amount));
    }

    private String generateAccountNumber() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
