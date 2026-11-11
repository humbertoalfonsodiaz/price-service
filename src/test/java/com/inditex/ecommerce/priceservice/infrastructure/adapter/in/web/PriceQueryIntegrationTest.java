package com.inditex.ecommerce.priceservice.infrastructure.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PriceQueryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest(name = "Query at {0} expects tariff {1} and price {2} {3}")
    @CsvSource({
            "2020-06-14T10:00:00, 1, 35.50, EUR, 2020-06-14T00:00:00, 2020-12-31T23:59:59",
            "2020-06-14T16:00:00, 2, 25.45, EUR, 2020-06-14T15:00:00, 2020-06-14T18:30:00",
            "2020-06-14T21:00:00, 1, 35.50, EUR, 2020-06-14T00:00:00, 2020-12-31T23:59:59",
            "2020-06-15T10:00:00, 3, 30.50, EUR, 2020-06-15T00:00:00, 2020-06-15T11:00:00",
            "2020-06-16T21:00:00, 4, 38.95, EUR, 2020-06-15T16:00:00, 2020-12-31T23:59:59"
    })
    @DisplayName("Verify pricing disambiguation and tariff resolution across target dates")
    void shouldResolveApplicableTariffs(String requestDate,
                                        int expectedTariff,
                                        double expectedPrice,
                                        String expectedCurrency,
                                        String expectedStart,
                                        String expectedEnd) throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .param("brandId", "1")
                        .param("productId", "35455")
                        .param("applicationDate", requestDate))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Correlation-Id"))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.priceList").value(expectedTariff))
                .andExpect(jsonPath("$.price").value(expectedPrice))
                .andExpect(jsonPath("$.currency").value(expectedCurrency))
                .andExpect(jsonPath("$.startDate").value(expectedStart))
                .andExpect(jsonPath("$.endDate").value(expectedEnd));
    }
}
