package com.govtech.scrabble.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Scrabble Points Calculator API")
                        .version("1.0.0")
                        .description("A comprehensive Spring Boot API for calculating Scrabble word scores, " +
                                   "finding optimal word combinations, and providing word scramble games with special tiles support."));
    }
}