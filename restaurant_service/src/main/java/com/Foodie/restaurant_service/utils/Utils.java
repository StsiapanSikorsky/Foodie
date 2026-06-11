package com.Foodie.restaurant_service.utils;

import com.Foodie.restaurant_service.advice.exceptions.NotFoundException;
import com.Foodie.restaurant_service.controllers.feignAuthenticationService.AuthServiceClient;
import com.Foodie.restaurant_service.entity.Restaurant;
import com.Foodie.restaurant_service.enums.ErrorMessage;
import com.Foodie.restaurant_service.enums.LogMessage;
import com.Foodie.restaurant_service.enums.UserRole;
import com.Foodie.restaurant_service.responce.authentication.AuthenticationRefreshResponse;
import com.Foodie.restaurant_service.responce.authentication.AuthenticationValidationResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class Utils {

    private final AuthServiceClient authServiceClient;

    public static String getMethodName(){
        try {
            return Thread.currentThread().getStackTrace()[1].getMethodName();
        }
        catch (Exception e) {
            log.debug(LogMessage.UNDEFINED_METHOD_NAME.getMessage());
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
        log.info(LogMessage.SET_COOKIE.getMessage(HttpHeaders.AUTHORIZATION));
        return authorizationCookie;
    }

    public static Cookie createRefreshTokenCookie(
            String value
    ){
        Cookie refreshtokenCookie = new Cookie("REFRESH_TOKEN", value);
        refreshtokenCookie.setHttpOnly(true);
        refreshtokenCookie.setSecure(true);
        refreshtokenCookie.setPath("/");
        refreshtokenCookie.setMaxAge(30 * 24 * 60 * 60);
        log.info(LogMessage.SET_COOKIE.getMessage("REFRESH_TOKEN"));
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

            log.info(LogMessage.GET_NEW_JWT_SUCCESS.getMessage());
            setCookie(response, refreshResponse);
        }
        return validationResponse;
    }

    public void setCookie(
            HttpServletResponse response,
            AuthenticationRefreshResponse refreshResponse
    ){
        Cookie authenticationCookie = Utils.createAuthenticationCookie(refreshResponse.getToken());
        Cookie refreshtokenCookie = Utils.createRefreshTokenCookie(refreshResponse.getRefreshToken());
        response.addCookie(authenticationCookie);
        response.addCookie(refreshtokenCookie);
    }

    public boolean checkRole(
            AuthenticationValidationResponse validationResponse
    ){
        List<String> roles = validationResponse.getRoles();
        if (roles == null || (!roles.contains(UserRole.OWNER.getRole()) && !roles.contains(UserRole.ADMIN.getRole())))
            return false;
        else
            return true;
    }

    public boolean isOwnerOrAdmin(
            Restaurant restaurant,
            AuthenticationValidationResponse validationResponse
    ){
        boolean isOwner = restaurant.getOwnerId().equals(validationResponse.getUserId());
        boolean isAdmin = validationResponse.getRoles() != null && validationResponse.getRoles().contains(UserRole.ADMIN.getRole());
        return isOwner || isAdmin;
    }

    public String checkTokensInCookie(
            String jwtToken,
            String refreshToken,
            HttpServletResponse response
    ){
        if(refreshToken == null) {
            log.warn(ErrorMessage.REFRESH_TOKEN_NOT_FOUND_IN_COOKIE.getMessage());
            throw new NotFoundException(ErrorMessage.REFRESH_TOKEN_NOT_FOUND_IN_COOKIE.getMessage());
        }
        if(jwtToken == null) {
            log.info(LogMessage.JWT_TOKEN_NOT_FOUND.getMessage());
            AuthenticationRefreshResponse refreshResponse = authServiceClient.refreshToken(refreshToken);
            String updatedJwt = "Bearer " + refreshResponse.getToken();
            AuthenticationValidationResponse validationResponse = authServiceClient.validateToken(updatedJwt);
            log.info(LogMessage.GET_NEW_JWT_SUCCESS.getMessage());

            setCookie(response, refreshResponse);
            return refreshResponse.getToken();
        }
        return jwtToken;
    }
}
