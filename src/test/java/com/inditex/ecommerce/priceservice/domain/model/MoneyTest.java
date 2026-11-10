package com.inditex.ecommerce.priceservice.domain.model;

import com.inditex.ecommerce.priceservice.domain.exception.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    @DisplayName("Should create Money and round to two decimal places with uppercase currency")
    void shouldCreateMoneyAndNormalizeScaleAndCurrency() {
        Money money = new Money(new BigDecimal("35.5"), "eur");

        assertThat(money.amount()).isEqualByComparingTo("35.50");
        assertThat(money.amount().scale()).isEqualTo(2);
        assertThat(money.currency()).isEqualTo("EUR");
    }

    @Test
    @DisplayName("Should throw exception when amount is null")
    void shouldThrowExceptionWhenAmountIsNull() {
        assertThatThrownBy(() -> new Money(null, "EUR"))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Amount cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when amount is negative")
    void shouldThrowExceptionWhenAmountIsNegative() {
        assertThatThrownBy(() -> new Money(new BigDecimal("-1.00"), "EUR"))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Amount cannot be negative");
    }

    @Test
    @DisplayName("Should throw exception when currency is invalid ISO code")
    void shouldThrowExceptionWhenCurrencyIsInvalid() {
        assertThatThrownBy(() -> new Money(new BigDecimal("10.00"), "INVALID"))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Invalid ISO 4217 currency code");
    }
}
