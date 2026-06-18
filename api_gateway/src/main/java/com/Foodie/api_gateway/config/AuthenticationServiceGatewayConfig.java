package com.Foodie.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthenticationServiceGatewayConfig {

    @Bean
    public RouteLocator authenticationRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path(
                                "/authentication/**",
                                "/owner/**",
                                "/user/**"
                        )
                        .uri("lb://AUTHENTICATION-SERVICE"))
                .build();
    }

}
