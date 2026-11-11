package com.inditex.ecommerce.priceservice.infrastructure.adapter.in.web;

import com.inditex.ecommerce.priceservice.application.port.in.GetApplicablePriceQuery;
import com.inditex.ecommerce.priceservice.application.port.in.GetApplicablePriceUseCase;
import com.inditex.ecommerce.priceservice.domain.exception.PriceNotFoundException;
import com.inditex.ecommerce.priceservice.domain.model.BrandId;
import com.inditex.ecommerce.priceservice.domain.model.DateRange;
import com.inditex.ecommerce.priceservice.domain.model.Money;
import com.inditex.ecommerce.priceservice.domain.model.Price;
import com.inditex.ecommerce.priceservice.domain.model.PriceListId;
import com.inditex.ecommerce.priceservice.domain.model.Priority;
import com.inditex.ecommerce.priceservice.domain.model.ProductId;
import com.inditex.ecommerce.priceservice.infrastructure.adapter.in.web.mapper.PriceWebMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PriceController.class)
@Import(PriceWebMapper.class)
class PriceControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetApplicablePriceUseCase getApplicablePriceUseCase;

    @Test
    @DisplayName("Should return 200 OK and price payload when price is found")
    void shouldReturnPriceSuccessfully() throws Exception {
        Price price = new Price(
                1L,
                new BrandId(1L),
                new ProductId(35455L),
                new PriceListId(1),
                new Priority(0),
                new DateRange(LocalDateTime.of(2020, 6, 14, 0, 0, 0), LocalDateTime.of(2020, 12, 31, 23, 59, 59)),
                Money.of(35.50, "EUR")
        );

        when(getApplicablePriceUseCase.execute(any(GetApplicablePriceQuery.class))).thenReturn(price);

        mockMvc.perform(get("/api/v1/prices")
                        .param("brandId", "1")
                        .param("productId", "35455")
                        .param("applicationDate", "2020-06-14T10:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.price").value(35.50))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    @DisplayName("Should return 404 ProblemDetail when PriceNotFoundException is thrown")
    void shouldReturn404WhenNotFound() throws Exception {
        when(getApplicablePriceUseCase.execute(any(GetApplicablePriceQuery.class)))
                .thenThrow(new PriceNotFoundException(new BrandId(1L), new ProductId(999L), LocalDateTime.now()));

        mockMvc.perform(get("/api/v1/prices")
                        .param("brandId", "1")
                        .param("productId", "999")
                        .param("applicationDate", "2020-06-14T10:00:00"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Price Not Found"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when parameter is missing")
    void shouldReturn400WhenMissingParameter() throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .param("brandId", "1")
                        .param("applicationDate", "2020-06-14T10:00:00"))
                .andExpect(status().isBadRequest());
    }
}
