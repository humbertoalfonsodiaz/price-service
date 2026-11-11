package com.inditex.ecommerce.priceservice.infrastructure.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should serve static OpenAPI specification successfully")
    void shouldServeStaticOpenApiSpec() throws Exception {
        mockMvc.perform(get("/openapi.yml"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should serve Swagger UI resource successfully")
    void shouldServeSwaggerUi() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should serve dynamic OpenAPI 3 JSON specification successfully at /api-docs")
    void shouldServeDynamicApiDocs() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should serve favicon.ico successfully")
    void shouldServeFavicon() throws Exception {
        mockMvc.perform(get("/favicon.ico"))
                .andExpect(status().isOk());
    }
}
