package com.Foodie.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthenticationServiceGatewayConfig {

    @Bean
    public RouteLocator authentRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/authentication/**")
                        .uri("http://localhost:8197"))
                .build();
    }

    @Bean
    public RouteLocator ownerRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/owner/**")
                        .uri("http://localhost:8197"))
                .build();
    }

    @Bean
    public RouteLocator userRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/user/**")
                        .uri("http://localhost:8197"))
                .build();
    }
}
