package com.Foodie.restaurant_service.controllers.feignAuthenticationService;

import com.Foodie.restaurant_service.responce.authentication.AuthenticationRefreshResponse;
import com.Foodie.restaurant_service.responce.authentication.AuthenticationValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "AUTHENTICATION-SERVICE")
public interface AuthServiceClient {

    @GetMapping("/authentication/validate")
    AuthenticationValidationResponse validateToken(
            @RequestHeader("Authorization") String token
    );

    @GetMapping("/authentication/refresh")
    AuthenticationRefreshResponse refreshToken(
            @RequestHeader("REFRESH_TOKEN") String refreshToken
    );
}
