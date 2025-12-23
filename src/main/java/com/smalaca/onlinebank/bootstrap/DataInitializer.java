package com.smalaca.onlinebank.bootstrap;

import com.smalaca.onlinebank.application.AccountService;
import com.smalaca.onlinebank.application.CustomerService;
import com.smalaca.onlinebank.domain.Currency;
import com.smalaca.onlinebank.domain.Account;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer {
    private final CustomerService customerService;
    private final AccountService accountService;

    public DataInitializer(CustomerService customerService, AccountService accountService) {
        this.customerService = customerService;
        this.accountService = accountService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        // Create 10 customers C001..C010
        List<String> customers = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            String number = "C" + String.format("%03d", i);
            customerService.addCustomer(number, "Customer " + i);
            customers.add(number);
        }

        // 5 customers with 1 account each
        int idx = 0;
        BigDecimal[] oneAccBalances = {bd(1000), bd(2000), bd(6340), bd(12), bd(123)};
        for (int i = 0; i < 5; i++) {
            Account acc = accountService.createAccount(customers.get(idx++), Currency.USD);
            accountService.deposit(acc.getAccountNumber(), oneAccBalances[i]);
        }

        // 2 customers with 2 accounts each
        BigDecimal[] twoAccBalances = {bd(10000), bd(20879), bd(892), bd(8231)};
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                Account acc = accountService.createAccount(customers.get(idx), Currency.USD);
                accountService.deposit(acc.getAccountNumber(), twoAccBalances[i * 2 + j]);
            }
            idx++;
        }

        // 3 customers with 3 accounts each
        BigDecimal[] threeAccBalances = {bd(2134), bd(4325), bd(5432), bd(1234), bd(5678), bd(98760)};
        int k = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Account acc = accountService.createAccount(customers.get(idx), Currency.USD);
                accountService.deposit(acc.getAccountNumber(), threeAccBalances[k % threeAccBalances.length]);
                k++;
            }
            idx++;
        }

        // Add 5 customers with NO accounts: C011..C015
        for (int i = 11; i <= 15; i++) {
            String number = "C" + String.format("%03d", i);
            customerService.addCustomer(number, "Customer " + i);
        }
    }

    private static BigDecimal bd(int v) { return new BigDecimal(v).setScale(4); }
}
