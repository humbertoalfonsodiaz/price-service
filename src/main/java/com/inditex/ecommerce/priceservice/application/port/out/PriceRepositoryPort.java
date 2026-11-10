package com.inditex.ecommerce.priceservice.application.port.out;

import com.inditex.ecommerce.priceservice.domain.model.BrandId;
import com.inditex.ecommerce.priceservice.domain.model.Price;
import com.inditex.ecommerce.priceservice.domain.model.ProductId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PriceRepositoryPort {

    Optional<Price> findApplicablePrice(BrandId brandId, ProductId productId, LocalDateTime applicationDate);

    List<Price> findAllApplicablePrices(BrandId brandId, ProductId productId, LocalDateTime applicationDate);
}
