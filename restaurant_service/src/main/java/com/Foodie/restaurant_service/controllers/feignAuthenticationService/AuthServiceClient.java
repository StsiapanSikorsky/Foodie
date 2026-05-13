package com.Foodie.restaurant_service.controllers.feignAuthenticationService;

import com.Foodie.restaurant_service.responce.authentication.AuthenticationValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "authenticationService",
        url = "${auth.service.url:http://localhost:8197}")
public interface AuthServiceClient {

    @GetMapping("/authentication/validate")
    AuthenticationValidationResponse validateToken(
            @RequestHeader("Authorization") String token
    );
}
