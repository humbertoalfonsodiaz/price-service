package com.inditex.ecommerce.priceservice.domain.service;

import com.inditex.ecommerce.priceservice.domain.model.Price;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class PriceDisambiguator {

    public Optional<Price> selectHighestPriority(List<Price> candidatePrices) {
        if (candidatePrices == null || candidatePrices.isEmpty()) {
            return Optional.empty();
        }

        return candidatePrices.stream()
                .max(Comparator.comparing(Price::getPriority));
    }
}
