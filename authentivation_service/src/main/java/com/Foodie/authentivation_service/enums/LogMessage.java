package com.Foodie.authentivation_service.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LogMessage {

    METHOD_API_CALLED("%s Called API method: %s"),

    REGISTER_USER_SUCCESS("Register new user with id:%s"),
    LOGIN_USER_SUCCESS("Success login user with id:%s"),
    REGISTER_OWNER_SUCCESS("Register new owner with id:%s"),
    LOGIN_OWNER_SUCCESS("Success login owner with id:%s"),

    TOKEN_CREATED_OR_UPDATED ("Token has be created or updated"),
    JWT_TOKEN_NOT_VALID("Jwt token not valid"),
    JWT_TOKEN_IS_VALID("Jwt token is valid"),
    GENERATE_NEW_JWT("New jwt token was generated for user with id:%s"),

    OWNER_WAS_UPDATED("Owner with id:%s was update"),
    OWNER_WAS_DELETED("Owner with id:%s was delete"),

    USER_WAS_UPDATED("User with id:%s was update"),
    USER_WAS_DELETED("User with id:%s was delete"),

    SET_COOKIE("Set cookie: %s"),

    UNDEFINED_METHOD_NAME("Method name was undefined"),
    ;

    private final String message;

    public String getMessage(Object ... args) {
        return String.format(message,args);
    }
}
