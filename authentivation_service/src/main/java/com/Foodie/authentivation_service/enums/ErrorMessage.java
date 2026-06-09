package com.Foodie.authentivation_service.enums;

import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public enum ErrorMessage {
    ROLE_NOT_FOUND("Role was not found"),
    USER_NOT_FOUND_BY_EMAIL("User with email %s was not found"),
    USER_NOT_FOUND_BY_ID("User with id %s was not found"),
    OWNER_NOT_FOUND_BY_ID("Owner with id %s was not found"),
    INVALID_USER_OR_PASSWORD("Invalid password or email"),
    USERNAME_ALREADY_EXISTS("Username %s already exists"),
    USER_EMAIL_ALREADY_EXISTS("Email %s already exists"),
    HAVE_NO_ACCESS("You dont have permissions for this event"),
    NOT_FOUND_REFRESH_TOKEN("Refresh token was not found"),

    HAVE_NO_ACCECSS("You don't have the necessary permissions"),
    TOKEN_EXPIRED("Token expired"),
    ERROR_DURING_JWT_PROCESSING("An unexpected error occurred during JWT processing"),
    UNEXPECTED_ERROR_OCCURRED("An unexpected error occured. Please try again later"),
    INVALID_TOKEN_SIGNATURE("INVALID_TOKEN_SIGNATURE"),
    INVALID_REFRESH_TOKEN("Invalid refresh token"),

    UNDEFINED("Undefined");


    private final String message;

    public String getMessage(Object ... args) {
        return String.format(message,args);
    }
}
