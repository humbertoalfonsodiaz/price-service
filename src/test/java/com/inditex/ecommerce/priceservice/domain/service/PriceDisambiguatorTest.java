package com.inditex.ecommerce.priceservice.domain.service;

import com.inditex.ecommerce.priceservice.domain.model.BrandId;
import com.inditex.ecommerce.priceservice.domain.model.DateRange;
import com.inditex.ecommerce.priceservice.domain.model.Money;
import com.inditex.ecommerce.priceservice.domain.model.Price;
import com.inditex.ecommerce.priceservice.domain.model.PriceListId;
import com.inditex.ecommerce.priceservice.domain.model.Priority;
import com.inditex.ecommerce.priceservice.domain.model.ProductId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PriceDisambiguatorTest {

    private final PriceDisambiguator disambiguator = new PriceDisambiguator();

    @Test
    @DisplayName("Should return empty optional when candidate prices list is null or empty")
    void shouldReturnEmptyWhenNoCandidates() {
        assertThat(disambiguator.selectHighestPriority(null)).isEmpty();
        assertThat(disambiguator.selectHighestPriority(Collections.emptyList())).isEmpty();
    }

    @Test
    @DisplayName("Should select the price with the highest numerical priority among overlapping candidates")
    void shouldSelectHighestPriorityPrice() {
        Price lowerPriorityPrice = new Price(
                1L,
                new BrandId(1L),
                new ProductId(35455L),
                new PriceListId(1),
                new Priority(0),
                new DateRange(LocalDateTime.of(2020, 6, 14, 0, 0, 0), LocalDateTime.of(2020, 12, 31, 23, 59, 59)),
                Money.of(35.50, "EUR")
        );

        Price higherPriorityPrice = new Price(
                2L,
                new BrandId(1L),
                new ProductId(35455L),
                new PriceListId(2),
                new Priority(1),
                new DateRange(LocalDateTime.of(2020, 6, 14, 15, 0, 0), LocalDateTime.of(2020, 6, 14, 18, 30, 0)),
                Money.of(25.45, "EUR")
        );

        Optional<Price> winningPrice = disambiguator.selectHighestPriority(List.of(lowerPriorityPrice, higherPriorityPrice));

        assertThat(winningPrice).isPresent();
        assertThat(winningPrice.get().getPriceListId().value()).isEqualTo(2);
        assertThat(winningPrice.get().getPriority().value()).isEqualTo(1);
        assertThat(winningPrice.get().getPrice().amount()).isEqualByComparingTo("25.45");
    }
}
