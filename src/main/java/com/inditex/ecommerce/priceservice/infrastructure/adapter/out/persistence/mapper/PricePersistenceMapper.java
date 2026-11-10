package com.inditex.ecommerce.priceservice.infrastructure.adapter.out.persistence.mapper;

import com.inditex.ecommerce.priceservice.domain.model.BrandId;
import com.inditex.ecommerce.priceservice.domain.model.DateRange;
import com.inditex.ecommerce.priceservice.domain.model.Money;
import com.inditex.ecommerce.priceservice.domain.model.Price;
import com.inditex.ecommerce.priceservice.domain.model.PriceListId;
import com.inditex.ecommerce.priceservice.domain.model.Priority;
import com.inditex.ecommerce.priceservice.domain.model.ProductId;
import com.inditex.ecommerce.priceservice.infrastructure.adapter.out.persistence.entity.PriceJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class PricePersistenceMapper {

    public Price toDomain(PriceJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Price(
                entity.getId(),
                new BrandId(entity.getBrandId()),
                new ProductId(entity.getProductId()),
                new PriceListId(entity.getPriceList()),
                new Priority(entity.getPriority()),
                new DateRange(entity.getStartDate(), entity.getEndDate()),
                new Money(entity.getPrice(), entity.getCurr())
        );
    }

    public PriceJpaEntity toEntity(Price domain) {
        if (domain == null) {
            return null;
        }

        return new PriceJpaEntity(
                domain.getId(),
                domain.getBrandId().value(),
                domain.getDateRange().startDate(),
                domain.getDateRange().endDate(),
                domain.getPriceListId().value(),
                domain.getProductId().value(),
                domain.getPriority().value(),
                domain.getPrice().amount(),
                domain.getPrice().currency()
        );
    }
}
