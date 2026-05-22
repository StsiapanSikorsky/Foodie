package com.Foodie.restaurant_service.utils;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Utils {

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
