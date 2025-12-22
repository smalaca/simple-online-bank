package com.smalaca.onlinebank.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String customerNumber;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts = new ArrayList<>();

    protected Customer() {}

    public Customer(String customerNumber, String name) {
        this.customerNumber = customerNumber;
        this.name = name;
    }

    public Long getId() { return id; }
    public String getCustomerNumber() { return customerNumber; }
    public String getName() { return name; }
    public List<Account> getAccounts() { return accounts; }
}
