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

    public Customer addCustomer(String name, String surname, String email, String phoneNumber, String address) {
        String customerNumber = generateCustomerNumber();
        return customers.save(new Customer(customerNumber, name, surname, email, phoneNumber, address));
    }

    private String generateCustomerNumber() {
        java.util.Random random = new java.util.Random();
        StringBuilder sb = new StringBuilder();
        
        // YY (2 letters)
        for (int i = 0; i < 2; i++) {
            sb.append((char) ('A' + random.nextInt(26)));
        }
        sb.append("-");
        
        // XX (2 numbers)
        for (int i = 0; i < 2; i++) {
            sb.append(random.nextInt(10));
        }
        sb.append("-");
        
        // XXXX-XXXX-XXXX-XXXX (4 blocks of 4 numbers)
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                sb.append(random.nextInt(10));
            }
            if (i < 3) {
                sb.append("-");
            }
        }
        
        return sb.toString();
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

    public Customer updateCustomer(String customerNumber, String name, String surname, String email, String phoneNumber, String address) {
        Customer customer = getByNumber(customerNumber);
        customer.update(name, surname, email, phoneNumber, address);
        return customers.save(customer);
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
