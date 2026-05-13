package com.Foodie.restaurant_service.utils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ErrorMessage {

    RESTAURANT_NOT_FOUND("Restaurant wth ID: %s was not found"),
    RESTAURANT_EXISTS_BY_NAME("Restaurant with Name: %s was created before"),

    USER_HASNOT_INCORRECT_ROLE("User hasnot incorrect role for this ivent: %s");

    private final String message;

    public String getMessage(Object... args){
        return String.format(message,args);
    }
}
