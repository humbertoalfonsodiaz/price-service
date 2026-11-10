package com.inditex.ecommerce.priceservice.infrastructure.adapter.out.persistence;

import com.inditex.ecommerce.priceservice.infrastructure.adapter.out.persistence.entity.PriceJpaEntity;
import com.inditex.ecommerce.priceservice.infrastructure.adapter.out.persistence.repository.SpringDataPriceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PriceJpaAdapterDataJpaTest {

    @Autowired
    private SpringDataPriceRepository repository;

    @Test
    @DisplayName("Should load 4 seeded prices from Flyway V2 migration")
    void shouldVerifySeededPricesExist() {
        List<PriceJpaEntity> all = repository.findAll();
        assertThat(all).hasSize(4);
    }

    @Test
    @DisplayName("Should return tariff 2 (priority 1) when overlapping with tariff 1 (priority 0) at 2020-06-14 16:00")
    void shouldReturnHighestPriorityTariffWhenOverlapping() {
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 16, 0, 0);

        List<PriceJpaEntity> applicable = repository.findAllApplicablePrices(1L, 35455L, date);
        assertThat(applicable).hasSize(2);
        assertThat(applicable.get(0).getPriceList()).isEqualTo(2); // highest priority first

        Optional<PriceJpaEntity> first = repository.findFirstApplicablePrice(1L, 35455L, date);
        assertThat(first).isPresent();
        assertThat(first.get().getPriceList()).isEqualTo(2);
        assertThat(first.get().getPrice()).isEqualByComparingTo(new BigDecimal("25.45"));
        assertThat(first.get().getPriority()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return empty when product does not exist")
    void shouldReturnEmptyForNonExistentProduct() {
        Optional<PriceJpaEntity> result = repository.findFirstApplicablePrice(1L, 99999L, LocalDateTime.now());
        assertThat(result).isEmpty();
    }
}
