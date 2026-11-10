package com.inditex.ecommerce.priceservice.application.port.in;

import com.inditex.ecommerce.priceservice.domain.exception.DomainValidationException;
import com.inditex.ecommerce.priceservice.domain.model.BrandId;
import com.inditex.ecommerce.priceservice.domain.model.ProductId;

import java.time.LocalDateTime;

public record GetApplicablePriceQuery(BrandId brandId, ProductId productId, LocalDateTime applicationDate) {

    public GetApplicablePriceQuery {
        if (brandId == null) {
            throw new DomainValidationException("BrandId cannot be null in query");
        }
        if (productId == null) {
            throw new DomainValidationException("ProductId cannot be null in query");
        }
        if (applicationDate == null) {
            throw new DomainValidationException("ApplicationDate cannot be null in query");
        }
    }
}
