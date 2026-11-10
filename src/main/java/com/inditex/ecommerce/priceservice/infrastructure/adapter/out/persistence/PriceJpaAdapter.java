package com.inditex.ecommerce.priceservice.infrastructure.adapter.out.persistence;

import com.inditex.ecommerce.priceservice.domain.model.BrandId;
import com.inditex.ecommerce.priceservice.domain.model.Price;
import com.inditex.ecommerce.priceservice.domain.model.ProductId;
import com.inditex.ecommerce.priceservice.application.port.out.PriceRepositoryPort;
import com.inditex.ecommerce.priceservice.infrastructure.adapter.out.persistence.mapper.PricePersistenceMapper;
import com.inditex.ecommerce.priceservice.infrastructure.adapter.out.persistence.repository.SpringDataPriceRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class PriceJpaAdapter implements PriceRepositoryPort {

    private final SpringDataPriceRepository repository;
    private final PricePersistenceMapper mapper;

    public PriceJpaAdapter(SpringDataPriceRepository repository, PricePersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Price> findApplicablePrice(BrandId brandId, ProductId productId, LocalDateTime applicationDate) {
        return repository.findFirstApplicablePrice(brandId.value(), productId.value(), applicationDate)
                .map(mapper::toDomain);
    }

    @Override
    public List<Price> findAllApplicablePrices(BrandId brandId, ProductId productId, LocalDateTime applicationDate) {
        return repository.findAllApplicablePrices(brandId.value(), productId.value(), applicationDate)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
