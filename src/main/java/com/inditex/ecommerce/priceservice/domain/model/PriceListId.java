package com.inditex.ecommerce.priceservice.domain.model;

import com.inditex.ecommerce.priceservice.domain.exception.DomainValidationException;

public record PriceListId(Integer value) {

    public PriceListId {
        if (value == null) {
            throw new DomainValidationException("PriceListId cannot be null");
        }
        if (value <= 0) {
            throw new DomainValidationException("PriceListId must be greater than zero, received: " + value);
        }
    }
}
