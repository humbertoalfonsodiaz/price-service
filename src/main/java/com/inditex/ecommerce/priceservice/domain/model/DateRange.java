package com.inditex.ecommerce.priceservice.domain.model;

import com.inditex.ecommerce.priceservice.domain.exception.DomainValidationException;

import java.time.LocalDateTime;

public record DateRange(LocalDateTime startDate, LocalDateTime endDate) {

    public DateRange {
        if (startDate == null) {
            throw new DomainValidationException("Start date cannot be null");
        }
        if (endDate == null) {
            throw new DomainValidationException("End date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new DomainValidationException(String.format(
                    "Start date (%s) cannot be after end date (%s)", startDate, endDate));
        }
    }

    public boolean contains(LocalDateTime target) {
        if (target == null) {
            return false;
        }
        return !target.isBefore(startDate) && !target.isAfter(endDate);
    }
}
