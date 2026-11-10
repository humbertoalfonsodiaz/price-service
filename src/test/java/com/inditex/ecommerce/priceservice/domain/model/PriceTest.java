package com.inditex.ecommerce.priceservice.domain.model;

import com.inditex.ecommerce.priceservice.domain.exception.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceTest {

    @Test
    @DisplayName("Should create valid Price and evaluate applicability and priority comparison")
    void shouldCreateValidPrice() {
        Price price1 = new Price(
                1L,
                new BrandId(1L),
                new ProductId(35455L),
                new PriceListId(1),
                new Priority(0),
                new DateRange(LocalDateTime.of(2020, 6, 14, 0, 0, 0), LocalDateTime.of(2020, 12, 31, 23, 59, 59)),
                Money.of(35.50, "EUR")
        );

        Price price2 = new Price(
                2L,
                new BrandId(1L),
                new ProductId(35455L),
                new PriceListId(2),
                new Priority(1),
                new DateRange(LocalDateTime.of(2020, 6, 14, 15, 0, 0), LocalDateTime.of(2020, 6, 14, 18, 30, 0)),
                Money.of(25.45, "EUR")
        );

        assertThat(price1.isApplicableAt(LocalDateTime.of(2020, 6, 14, 10, 0, 0))).isTrue();
        assertThat(price2.isApplicableAt(LocalDateTime.of(2020, 6, 14, 10, 0, 0))).isFalse();
        assertThat(price2.hasHigherPriorityThan(price1)).isTrue();
        assertThat(price1.hasHigherPriorityThan(price2)).isFalse();
    }

    @Test
    @DisplayName("Should throw exception when mandatory fields are null")
    void shouldThrowExceptionWhenFieldsAreNull() {
        assertThatThrownBy(() -> new Price(null, null, new ProductId(1L), new PriceListId(1), new Priority(0),
                new DateRange(LocalDateTime.now(), LocalDateTime.now().plusDays(1)), Money.of(10.0, "EUR")))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("BrandId cannot be null");
    }
}
