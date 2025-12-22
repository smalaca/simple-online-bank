package com.smalaca.onlinebank.application;

import com.smalaca.onlinebank.domain.Customer;
import com.smalaca.onlinebank.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class CustomerService {
    private final CustomerRepository customers;

    public CustomerService(CustomerRepository customers) {
        this.customers = customers;
    }

    public Customer addCustomer(String customerNumber, String name) {
        return customers.save(new Customer(customerNumber, name));
    }

    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        return customers.findAll();
    }

    @Transactional(readOnly = true)
    public Customer getByNumber(String number) {
        return customers.findByCustomerNumber(number)
                .orElseThrow(() -> new NoSuchElementException("Customer not found: " + number));
    }
}
