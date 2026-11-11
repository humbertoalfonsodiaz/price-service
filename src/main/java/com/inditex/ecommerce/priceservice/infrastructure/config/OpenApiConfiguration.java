package com.inditex.ecommerce.priceservice.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI priceServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inditex E-Commerce Price Service API")
                        .description("Microservice REST API for querying applicable product prices based on brand, product, and application date with priority disambiguation.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Inditex E-Commerce Architecture Team")
                                .email("ecommerce-architecture@inditex.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local / Docker server")
                ));
    }
}
