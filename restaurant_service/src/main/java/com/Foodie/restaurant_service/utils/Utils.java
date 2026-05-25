package com.Foodie.restaurant_service.utils;

import com.Foodie.restaurant_service.advice.exceptions.NotFoundException;
import com.Foodie.restaurant_service.controllers.feignAuthenticationService.AuthServiceClient;
import com.Foodie.restaurant_service.entity.Restaurant;
import com.Foodie.restaurant_service.responce.authentication.AuthenticationRefreshResponse;
import com.Foodie.restaurant_service.responce.authentication.AuthenticationValidationResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Utils {

    private final AuthServiceClient authServiceClient;

    public static String getMethodName(){
        try {
            return Thread.currentThread().getStackTrace()[1].getMethodName();
        }
        catch (Exception e) {
            return ErrorMessage.UNDEFINED.getMessage();
        }
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

    public boolean checkRole(
            AuthenticationValidationResponse validationResponse
    ){
        List<String> roles = validationResponse.getRoles();
        if (roles == null || (!roles.contains("OWNER") && !roles.contains("ADMIN")))
            return false;
        else
            return true;
    }

    public boolean isOwnerOrAdmin(
            Restaurant restaurant,
            AuthenticationValidationResponse validationResponse
    ){
        boolean isOwner = restaurant.getOwnerId().equals(validationResponse.getUserId());
        boolean isAdmin = validationResponse.getRoles() != null && validationResponse.getRoles().contains("ADMIN");
        return isOwner || isAdmin;
    }

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
}
