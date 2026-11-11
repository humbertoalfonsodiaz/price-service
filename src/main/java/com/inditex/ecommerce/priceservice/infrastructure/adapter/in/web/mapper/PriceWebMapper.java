package com.inditex.ecommerce.priceservice.infrastructure.adapter.in.web.mapper;

import com.inditex.ecommerce.priceservice.domain.model.Price;
import com.inditex.ecommerce.priceservice.infrastructure.adapter.in.web.dto.PriceResponse;
import org.springframework.stereotype.Component;

@Component
public class PriceWebMapper {

    public PriceResponse toResponse(Price price) {
        if (price == null) {
            return null;
        }

        PriceResponse response = new PriceResponse();
        response.setProductId(price.getProductId().value());
        response.setBrandId(price.getBrandId().value());
        response.setPriceList(price.getPriceListId().value());
        response.setStartDate(price.getDateRange().startDate());
        response.setEndDate(price.getDateRange().endDate());
        response.setPrice(price.getPrice().amount().doubleValue());
        response.setCurrency(price.getPrice().currency());
        return response;
    }
}
