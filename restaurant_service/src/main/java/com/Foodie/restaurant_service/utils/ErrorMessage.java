package com.Foodie.restaurant_service.utils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ErrorMessage {

    RESTAURANT_NOT_FOUND("Restaurant wth ID: %s was not found"),
    RESTAURANT_EXISTS_BY_NAME("Restaurant with Name: %s was created before"),

    USER_HAS_NOT_INCORRECT_ROLE("User has not incorrect role for this ivent: %s"),
    USER_ROLE_HAS_NOT_VALID("Only owners can create restaurants"),
    INCORRECT_OWNER("You don't have permission to delete this restaurant"),
    TOKEN_NOT_FOUND_IN_COOKIE("Token not found in cookie"),
    REFRESH_TOKEN_NOT_FOUND_IN_COOKIE("Refresh token not found in cookie");

    private final String message;

    public String getMessage(Object... args){
        return String.format(message,args);
    }
}
