package com.inditex.ecommerce.priceservice.infrastructure.adapter.in.web;

import io.karatelabs.junit6.Karate;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PricesKarateIntegrationTest {

    @LocalServerPort
    private int serverPort;

    @BeforeEach
    void setUp() {
        System.setProperty("karate.server.port", String.valueOf(serverPort));
    }

    @Karate.Test
    Karate testPrices() {
        return Karate.run("classpath:karate/prices.feature");
    }
}
