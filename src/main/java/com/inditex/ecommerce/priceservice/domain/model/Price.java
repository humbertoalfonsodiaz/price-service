package com.inditex.ecommerce.priceservice.domain.model;

import com.inditex.ecommerce.priceservice.domain.exception.DomainValidationException;

import java.time.LocalDateTime;
import java.util.Objects;

public class Price {

    private final Long id;
    private final BrandId brandId;
    private final ProductId productId;
    private final PriceListId priceListId;
    private final Priority priority;
    private final DateRange dateRange;
    private final Money price;

    public Price(Long id,
                 BrandId brandId,
                 ProductId productId,
                 PriceListId priceListId,
                 Priority priority,
                 DateRange dateRange,
                 Money price) {
        if (brandId == null) {
            throw new DomainValidationException("BrandId cannot be null in Price");
        }
        if (productId == null) {
            throw new DomainValidationException("ProductId cannot be null in Price");
        }
        if (priceListId == null) {
            throw new DomainValidationException("PriceListId cannot be null in Price");
        }
        if (priority == null) {
            throw new DomainValidationException("Priority cannot be null in Price");
        }
        if (dateRange == null) {
            throw new DomainValidationException("DateRange cannot be null in Price");
        }
        if (price == null) {
            throw new DomainValidationException("Money price cannot be null in Price");
        }

        this.id = id;
        this.brandId = brandId;
        this.productId = productId;
        this.priceListId = priceListId;
        this.priority = priority;
        this.dateRange = dateRange;
        this.price = price;
    }

    public boolean isApplicableAt(LocalDateTime applicationDate) {
        return this.dateRange.contains(applicationDate);
    }

    public boolean hasHigherPriorityThan(Price other) {
        if (other == null) {
            return true;
        }
        return this.priority.compareTo(other.getPriority()) > 0;
    }

    public Long getId() {
        return id;
    }

    public BrandId getBrandId() {
        return brandId;
    }

    public ProductId getProductId() {
        return productId;
    }

    public PriceListId getPriceListId() {
        return priceListId;
    }

    public Priority getPriority() {
        return priority;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public Money getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price1 = (Price) o;
        return Objects.equals(id, price1.id) &&
                Objects.equals(brandId, price1.brandId) &&
                Objects.equals(productId, price1.productId) &&
                Objects.equals(priceListId, price1.priceListId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, brandId, productId, priceListId);
    }

    @Override
    public String toString() {
        return "Price{" +
                "id=" + id +
                ", brandId=" + brandId +
                ", productId=" + productId +
                ", priceListId=" + priceListId +
                ", priority=" + priority +
                ", dateRange=" + dateRange +
                ", price=" + price +
                '}';
    }
}
