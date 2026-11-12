package com.inditex.ecommerce.priceservice.application.service;

import com.inditex.ecommerce.priceservice.application.port.in.GetApplicablePriceQuery;
import com.inditex.ecommerce.priceservice.application.port.in.GetApplicablePriceUseCase;
import com.inditex.ecommerce.priceservice.domain.exception.PriceNotFoundException;
import com.inditex.ecommerce.priceservice.domain.model.BrandId;
import com.inditex.ecommerce.priceservice.domain.model.Price;
import com.inditex.ecommerce.priceservice.domain.model.ProductId;
import com.inditex.ecommerce.priceservice.application.port.out.PriceRepositoryPort;
import com.inditex.ecommerce.priceservice.domain.service.PriceDisambiguator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

public class GetApplicablePriceService implements GetApplicablePriceUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetApplicablePriceService.class);

    private final PriceRepositoryPort priceRepositoryPort;
    private final PriceDisambiguator priceDisambiguator;

    public GetApplicablePriceService(PriceRepositoryPort priceRepositoryPort,
                                     PriceDisambiguator priceDisambiguator) {
        this.priceRepositoryPort = priceRepositoryPort;
        this.priceDisambiguator = priceDisambiguator;
    }

    @Override
    public Price execute(GetApplicablePriceQuery query) {
        BrandId brandId = query.brandId();
        ProductId productId = query.productId();
        LocalDateTime applicationDate = query.applicationDate();

        log.debug("Executing GetApplicablePrice for brandId={}, productId={}, applicationDate={}",
                brandId.value(), productId.value(), applicationDate);

        List<Price> candidates = priceRepositoryPort.findAllApplicablePrices(
                brandId, productId, applicationDate);

        return priceDisambiguator.selectHighestPriority(candidates)
                .orElseThrow(() -> {
                    log.warn("No price found for brandId={}, productId={}, applicationDate={}",
                            brandId.value(), productId.value(), applicationDate);
                    return new PriceNotFoundException(brandId, productId, applicationDate);
                });
    }
}
