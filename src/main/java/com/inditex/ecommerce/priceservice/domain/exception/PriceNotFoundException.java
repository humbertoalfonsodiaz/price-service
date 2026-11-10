package com.inditex.ecommerce.priceservice.domain.exception;

import com.inditex.ecommerce.priceservice.domain.model.BrandId;
import com.inditex.ecommerce.priceservice.domain.model.ProductId;

import java.time.LocalDateTime;

public class PriceNotFoundException extends RuntimeException {

    private final BrandId brandId;
    private final ProductId productId;
    private final LocalDateTime applicationDate;

    public PriceNotFoundException(BrandId brandId, ProductId productId, LocalDateTime applicationDate) {
        super(String.format("No applicable price found for brandId=%s, productId=%s at %s",
                brandId != null ? brandId.value() : "null",
                productId != null ? productId.value() : "null",
                applicationDate));
        this.brandId = brandId;
        this.productId = productId;
        this.applicationDate = applicationDate;
    }

    public BrandId getBrandId() {
        return brandId;
    }

    public ProductId getProductId() {
        return productId;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }
}
