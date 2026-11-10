package com.inditex.ecommerce.priceservice.application.port.in;

import com.inditex.ecommerce.priceservice.domain.model.Price;

public interface GetApplicablePriceUseCase {

    Price execute(GetApplicablePriceQuery query);
}
