package com.inditex.ecommerce.priceservice.domain.model;

import com.inditex.ecommerce.priceservice.domain.exception.DomainValidationException;

public record Priority(Integer value) implements Comparable<Priority> {

    public Priority {
        if (value == null) {
            throw new DomainValidationException("Priority cannot be null");
        }
        if (value < 0) {
            throw new DomainValidationException("Priority cannot be negative, received: " + value);
        }
    }

    @Override
    public int compareTo(Priority other) {
        if (other == null) {
            return 1;
        }
        return this.value.compareTo(other.value);
    }
}
