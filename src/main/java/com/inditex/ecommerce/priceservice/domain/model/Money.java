package com.inditex.ecommerce.priceservice.domain.model;

import com.inditex.ecommerce.priceservice.domain.exception.DomainValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public record Money(BigDecimal amount, String currency) {

    public Money {
        if (amount == null) {
            throw new DomainValidationException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainValidationException("Amount cannot be negative, received: " + amount);
        }
        if (currency == null || currency.isBlank()) {
            throw new DomainValidationException("Currency cannot be null or blank");
        }
        try {
            Currency.getInstance(currency.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DomainValidationException("Invalid ISO 4217 currency code: " + currency);
        }

        amount = amount.setScale(2, RoundingMode.HALF_UP);
        currency = currency.trim().toUpperCase();
    }

    public static Money of(double amount, String currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }
}
