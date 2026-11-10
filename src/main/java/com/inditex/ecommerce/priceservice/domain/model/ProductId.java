package com.inditex.ecommerce.priceservice.domain.model;

import com.inditex.ecommerce.priceservice.domain.exception.DomainValidationException;

public record ProductId(Long value) {

    public ProductId {
        if (value == null) {
            throw new DomainValidationException("ProductId cannot be null");
        }
        if (value <= 0) {
            throw new DomainValidationException("ProductId must be greater than zero, received: " + value);
        }
    }
}
