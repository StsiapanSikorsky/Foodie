package com.Foodie.authentivation_service.utils;

import com.Foodie.authentivation_service.enums.ErrorMessage;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor
public class Utils {

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
}
