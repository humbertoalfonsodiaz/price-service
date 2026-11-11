package com.inditex.ecommerce.priceservice.infrastructure.adapter.in.web;

import com.inditex.ecommerce.priceservice.application.port.in.GetApplicablePriceQuery;
import com.inditex.ecommerce.priceservice.application.port.in.GetApplicablePriceUseCase;
import com.inditex.ecommerce.priceservice.domain.model.BrandId;
import com.inditex.ecommerce.priceservice.domain.model.Price;
import com.inditex.ecommerce.priceservice.domain.model.ProductId;
import com.inditex.ecommerce.priceservice.infrastructure.adapter.in.web.api.PricesApi;
import com.inditex.ecommerce.priceservice.infrastructure.adapter.in.web.dto.PriceResponse;
import com.inditex.ecommerce.priceservice.infrastructure.adapter.in.web.mapper.PriceWebMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class PriceController implements PricesApi {

    private static final Logger log = LoggerFactory.getLogger(PriceController.class);

    private final GetApplicablePriceUseCase getApplicablePriceUseCase;
    private final PriceWebMapper mapper;

    public PriceController(GetApplicablePriceUseCase getApplicablePriceUseCase, PriceWebMapper mapper) {
        this.getApplicablePriceUseCase = getApplicablePriceUseCase;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<PriceResponse> getApplicablePrice(LocalDateTime applicationDate,
                                                          Long productId,
                                                          Long brandId) {
        log.info("REST request to get applicable price: brandId={}, productId={}, applicationDate={}",
                brandId, productId, applicationDate);

        GetApplicablePriceQuery query = new GetApplicablePriceQuery(
                new BrandId(brandId),
                new ProductId(productId),
                applicationDate
        );

        Price price = getApplicablePriceUseCase.execute(query);
        PriceResponse response = mapper.toResponse(price);

        log.info("Applicable price resolved: tariff={}, price={} {}",
                response.getPriceList(), response.getPrice(), response.getCurrency());

        return ResponseEntity.ok(response);
    }
}
