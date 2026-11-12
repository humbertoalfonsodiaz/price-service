package com.inditex.ecommerce.priceservice.application.service;

import com.inditex.ecommerce.priceservice.application.port.in.GetApplicablePriceQuery;
import com.inditex.ecommerce.priceservice.domain.exception.PriceNotFoundException;
import com.inditex.ecommerce.priceservice.domain.model.BrandId;
import com.inditex.ecommerce.priceservice.domain.model.DateRange;
import com.inditex.ecommerce.priceservice.domain.model.Money;
import com.inditex.ecommerce.priceservice.domain.model.Price;
import com.inditex.ecommerce.priceservice.domain.model.PriceListId;
import com.inditex.ecommerce.priceservice.domain.model.Priority;
import com.inditex.ecommerce.priceservice.domain.model.ProductId;
import com.inditex.ecommerce.priceservice.application.port.out.PriceRepositoryPort;
import com.inditex.ecommerce.priceservice.domain.service.PriceDisambiguator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetApplicablePriceServiceTest {

    @Mock
    private PriceRepositoryPort priceRepositoryPort;

    private final PriceDisambiguator priceDisambiguator = new PriceDisambiguator();
    private GetApplicablePriceService useCase;

    private final BrandId brandId = new BrandId(1L);
    private final ProductId productId = new ProductId(35455L);
    private final LocalDateTime date = LocalDateTime.of(2020, 6, 14, 10, 0, 0);

    @BeforeEach
    void setUp() {
        useCase = new GetApplicablePriceService(priceRepositoryPort, priceDisambiguator);
    }

    @Test
    @DisplayName("Should return price when repository port finds a single applicable price")
    void shouldReturnPriceWhenSingleCandidateFound() {
        Price expectedPrice = new Price(
                1L,
                brandId,
                productId,
                new PriceListId(1),
                new Priority(0),
                new DateRange(LocalDateTime.of(2020, 6, 14, 0, 0, 0), LocalDateTime.of(2020, 12, 31, 23, 59, 59)),
                Money.of(35.50, "EUR")
        );

        when(priceRepositoryPort.findAllApplicablePrices(brandId, productId, date))
                .thenReturn(List.of(expectedPrice));

        Price result = useCase.execute(new GetApplicablePriceQuery(brandId, productId, date));

        assertThat(result).isEqualTo(expectedPrice);
        verify(priceRepositoryPort).findAllApplicablePrices(brandId, productId, date);
        verifyNoMoreInteractions(priceRepositoryPort);
    }

    @Test
    @DisplayName("Should select highest priority tariff when candidate list has overlapping matches")
    void shouldDisambiguateOverlappingCandidatesByHighestPriority() {
        Price price1 = new Price(
                1L,
                brandId,
                productId,
                new PriceListId(1),
                new Priority(0),
                new DateRange(LocalDateTime.of(2020, 6, 14, 0, 0, 0), LocalDateTime.of(2020, 12, 31, 23, 59, 59)),
                Money.of(35.50, "EUR")
        );

        Price price2 = new Price(
                2L,
                brandId,
                productId,
                new PriceListId(2),
                new Priority(1),
                new DateRange(LocalDateTime.of(2020, 6, 14, 15, 0, 0), LocalDateTime.of(2020, 6, 14, 18, 30, 0)),
                Money.of(25.45, "EUR")
        );

        when(priceRepositoryPort.findAllApplicablePrices(brandId, productId, date)).thenReturn(List.of(price1, price2));

        Price result = useCase.execute(new GetApplicablePriceQuery(brandId, productId, date));

        assertThat(result).isEqualTo(price2);
        verify(priceRepositoryPort).findAllApplicablePrices(brandId, productId, date);
        verifyNoMoreInteractions(priceRepositoryPort);
    }

    @Test
    @DisplayName("Should throw PriceNotFoundException with a single repository lookup when no prices are found")
    void shouldThrowPriceNotFoundExceptionWhenNotFound() {
        when(priceRepositoryPort.findAllApplicablePrices(brandId, productId, date)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> useCase.execute(new GetApplicablePriceQuery(brandId, productId, date)))
                .isInstanceOf(PriceNotFoundException.class)
                .hasMessageContaining("No applicable price found");

        verify(priceRepositoryPort).findAllApplicablePrices(brandId, productId, date);
        verifyNoMoreInteractions(priceRepositoryPort);
    }
}
