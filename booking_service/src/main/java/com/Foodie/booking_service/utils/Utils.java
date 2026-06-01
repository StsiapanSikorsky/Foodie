package com.Foodie.booking_service.utils;

import com.Foodie.booking_service.advice.exception.NotFoundException;
import com.Foodie.booking_service.controllers.feignAuthenticationService.AuthenticationServiceClient;
import com.Foodie.booking_service.response.authentication.AuthenticationRefreshResponse;
import com.Foodie.booking_service.response.authentication.AuthenticationValidationResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;

@RequiredArgsConstructor
public class Utils {

    private final AuthenticationServiceClient authServiceClient;

    public static void checkTokensInCookie(
            String jwtToken,
            String refreshToken
    ){
        if(jwtToken == null) {
            throw new NotFoundException("Jwt token not found in cookie");
        }

        if(refreshToken == null) {
            throw new NotFoundException("Refresh token not found in cookie");
        }
    }

    public AuthenticationValidationResponse checkValidTokens(
            String jwtToken,
            String refreshToken,
            HttpServletResponse response
    ){
        AuthenticationValidationResponse validationResponse;

        validationResponse = authServiceClient.validateToken(jwtToken);

        if (!validationResponse.isValid()){
            AuthenticationRefreshResponse refreshResponse = authServiceClient.refreshToken(refreshToken);
            String updatedJwt = "Bearer " + refreshResponse.getToken();
            validationResponse = authServiceClient.validateToken(updatedJwt);

            setCookie(response, refreshResponse);
        }
        return validationResponse;
    }

    public void setCookie(
            HttpServletResponse response,
            AuthenticationRefreshResponse refreshResponse
    ){
        Cookie authenticationCookie = Utils.createAuthenticationCookie(refreshResponse.getToken());
        Cookie refreshtokenCookie = Utils.creauteRefreshTokenCookie(refreshResponse.getRefreshToken());
        response.addCookie(authenticationCookie);
        response.addCookie(refreshtokenCookie);
    }

    public static Cookie createAuthenticationCookie(
            String value
    ){
        Cookie authorizationCookie = new Cookie(HttpHeaders.AUTHORIZATION, value);
        authorizationCookie.setHttpOnly(true);
        authorizationCookie.setSecure(true);
        authorizationCookie.setPath("/");
        authorizationCookie.setMaxAge(15 * 60);
        return authorizationCookie;
    }

    public static Cookie creauteRefreshTokenCookie(
            String value
    ){
        Cookie refreshtokenCookie = new Cookie("REFRESH_TOKEN", value);
        refreshtokenCookie.setHttpOnly(true);
        refreshtokenCookie.setSecure(true);
        refreshtokenCookie.setPath("/");
        refreshtokenCookie.setMaxAge(30 * 24 * 60 * 60);
        return refreshtokenCookie;
    }
}
