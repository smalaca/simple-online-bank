package com.smalaca.onlinebank.repository;

import com.smalaca.onlinebank.domain.Account;
import com.smalaca.onlinebank.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByCustomer(Customer customer);
}
