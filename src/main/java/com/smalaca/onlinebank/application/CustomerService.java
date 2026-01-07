package com.smalaca.onlinebank.application;

import com.smalaca.onlinebank.domain.Customer;
import com.smalaca.onlinebank.repository.AccountRepository;
import com.smalaca.onlinebank.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class CustomerService {
    private final CustomerRepository customers;
    private final AccountRepository accounts;

    public CustomerService(CustomerRepository customers, AccountRepository accounts) {
        this.customers = customers;
        this.accounts = accounts;
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

    public CustomerDeletionResult deleteCustomer(String customerNumber) {
        Customer customer = getByNumber(customerNumber);

        if (!accounts.findByCustomer(customer).isEmpty()) {
            return CustomerDeletionResult.error(customerNumber);
        }

        customers.delete(customer);
        return CustomerDeletionResult.success(customerNumber);
    }

    public record CustomerDeletionResult(boolean success, String customerNumber) {
        public static CustomerDeletionResult success(String customerNumber) {
            return new CustomerDeletionResult(true, customerNumber);
        }

        public static CustomerDeletionResult error(String customerNumber) {
            return new CustomerDeletionResult(false, customerNumber);
        }
    }
}
