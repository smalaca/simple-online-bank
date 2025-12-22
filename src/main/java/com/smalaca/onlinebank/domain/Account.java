package com.smalaca.onlinebank.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @ManyToOne(optional = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance = BigDecimal.ZERO;

    protected Account() {}

    public Account(String accountNumber, Customer customer, Currency currency) {
        this.accountNumber = accountNumber;
        this.customer = customer;
        this.currency = currency;
        this.balance = BigDecimal.ZERO;
    }

    public Long getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public Customer getCustomer() { return customer; }
    public Currency getCurrency() { return currency; }
    public BigDecimal getBalance() { return balance; }

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void debit(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
        if (this.balance.scale() < 4) {
            this.balance = this.balance.setScale(4);
        }
    }
}
