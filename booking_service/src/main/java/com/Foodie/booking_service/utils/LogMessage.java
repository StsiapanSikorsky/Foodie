package com.Foodie.booking_service.utils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LogMessage {

    //API logs
    METHOD_API_CALLED("Called API method: %s"),

    //Methods logs
    USER_CREATE_NEW_BOOKING("User with id: %s create booking in restaurant with id: %s. Number of table: %s (%s guests)"),
    RESULT_RETURN_FROM_CACHE("Result was return from cache"),
    RESULT_RETURN_FROM_DB("Result was return from DB"),
    UPDATE_BOOKING_SUCCESS("User with id: %s update booking with id: %s"),
    BOOKING_STATUS_IS_CANCELLED("The booking status was changed in %s"),

;
    private final String message;

    public String getMessage(Object... args){
        return String.format(message, args);
    }
}
