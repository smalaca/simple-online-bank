package com.smalaca.onlinebank.application;

import com.smalaca.onlinebank.domain.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;

public class RatesEngine {
    private static final Map<Currency, BigDecimal> USD_BASE = new EnumMap<>(Currency.class);

    static {
        // Fake rates relative to USD
        USD_BASE.put(Currency.USD, BigDecimal.ONE);
        USD_BASE.put(Currency.EUR, new BigDecimal("0.9"));
        USD_BASE.put(Currency.GBP, new BigDecimal("0.8"));
    }

    public BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
        if (from == to) return amount;
        BigDecimal inUsd = amount.divide(USD_BASE.get(from), 8, RoundingMode.HALF_UP);
        BigDecimal target = inUsd.multiply(USD_BASE.get(to));
        return target.setScale(4, RoundingMode.HALF_UP);
    }
}
