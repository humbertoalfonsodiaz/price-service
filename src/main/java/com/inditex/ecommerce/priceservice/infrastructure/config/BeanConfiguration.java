package com.inditex.ecommerce.priceservice.infrastructure.config;

import com.inditex.ecommerce.priceservice.application.port.in.GetApplicablePriceUseCase;
import com.inditex.ecommerce.priceservice.application.service.GetApplicablePriceService;
import com.inditex.ecommerce.priceservice.application.port.out.PriceRepositoryPort;
import com.inditex.ecommerce.priceservice.domain.service.PriceDisambiguator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public PriceDisambiguator priceDisambiguator() {
        return new PriceDisambiguator();
    }

    @Bean
    public GetApplicablePriceUseCase getApplicablePriceUseCase(PriceRepositoryPort priceRepositoryPort,
                                                               PriceDisambiguator priceDisambiguator) {
        return new GetApplicablePriceService(priceRepositoryPort, priceDisambiguator);
    }
}
