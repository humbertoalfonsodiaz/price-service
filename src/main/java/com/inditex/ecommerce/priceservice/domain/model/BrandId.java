package com.inditex.ecommerce.priceservice.domain.model;

import com.inditex.ecommerce.priceservice.domain.exception.DomainValidationException;

public record BrandId(Long value) {

    public BrandId {
        if (value == null) {
            throw new DomainValidationException("BrandId cannot be null");
        }
        if (value <= 0) {
            throw new DomainValidationException("BrandId must be greater than zero, received: " + value);
        }
    }
}
