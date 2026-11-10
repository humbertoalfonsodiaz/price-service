package com.inditex.ecommerce.priceservice.domain.model;

import com.inditex.ecommerce.priceservice.domain.exception.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DateRangeTest {

    private final LocalDateTime start = LocalDateTime.of(2020, 6, 14, 15, 0, 0);
    private final LocalDateTime end = LocalDateTime.of(2020, 6, 14, 18, 30, 0);

    @Test
    @DisplayName("Should correctly evaluate inclusive containment of dates")
    void shouldCorrectlyEvaluateContainment() {
        DateRange range = new DateRange(start, end);

        assertThat(range.contains(start)).isTrue();
        assertThat(range.contains(end)).isTrue();
        assertThat(range.contains(LocalDateTime.of(2020, 6, 14, 16, 0, 0))).isTrue();

        assertThat(range.contains(LocalDateTime.of(2020, 6, 14, 14, 59, 59))).isFalse();
        assertThat(range.contains(LocalDateTime.of(2020, 6, 14, 18, 30, 1))).isFalse();
        assertThat(range.contains(null)).isFalse();
    }

    @Test
    @DisplayName("Should throw exception when start date is after end date")
    void shouldThrowExceptionWhenStartIsAfterEnd() {
        assertThatThrownBy(() -> new DateRange(end, start))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("cannot be after end date");
    }

    @Test
    @DisplayName("Should throw exception when dates are null")
    void shouldThrowExceptionWhenDateIsNull() {
        assertThatThrownBy(() -> new DateRange(null, end))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Start date cannot be null");

        assertThatThrownBy(() -> new DateRange(start, null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("End date cannot be null");
    }
}
