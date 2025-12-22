package com.smalaca.onlinebank.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/offers/accounts")
public class OffersController {

    public record AccountOffer(String name, String currency, BigDecimal interestRateAnnual, BigDecimal monthlyFee) {}

    @GetMapping
    public List<AccountOffer> getAccountOffers() {
        return List.of(
                new AccountOffer("Everyday Account", "USD", new BigDecimal("0.0000"), new BigDecimal("0.00")),
                new AccountOffer("Savings Account", "USD", new BigDecimal("0.0150"), new BigDecimal("0.00")),
                new AccountOffer("Premium Account", "USD", new BigDecimal("0.0050"), new BigDecimal("9.99"))
        );
    }
}
