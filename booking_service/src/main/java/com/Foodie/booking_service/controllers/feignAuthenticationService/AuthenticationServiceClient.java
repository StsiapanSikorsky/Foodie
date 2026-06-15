package com.Foodie.booking_service.controllers.feignAuthenticationService;

import com.Foodie.booking_service.response.authentication.AuthenticationRefreshResponse;
import com.Foodie.booking_service.response.authentication.AuthenticationValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "AUTHENTICATION-SERVICE")
public interface AuthenticationServiceClient {

    @GetMapping("${end.point.authentication.validate}")
    AuthenticationValidationResponse validateToken(
            @RequestHeader("Authorization") String token
    );

    @GetMapping("${end.point.authentication.refresh}")
    AuthenticationRefreshResponse refreshToken(
            @RequestHeader("REFRESH_TOKEN") String refreshToken
    );
}
