package com.innov4africa.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Value("${ishop.base-url}")
    private String ishopBaseUrl;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("ipay_route", r -> r.path("/ipay/**")
                        .uri("http://localhost:8081"))
                .route("ibanking_route", r -> r.path("/ibanking/**")
                        .uri("http://localhost:8082"))
                .route("ishop_route", r -> r.path("/mobile-ws/**")
                        .uri(ishopBaseUrl))
                .build();
    }
}