package com.Foodie.booking_service.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ErrorMessage {
    BOOKING_CONFLICT("Booking with you time was busy"),
    GUESTS_CONFLICT("Table has %s max guests"),
    BOOKING_NOT_FOUND_BY_ID("Booking with id: %s was not found"),
    INCORRECT_ROLE("You role is forbidden for this event. Yours roles: %s"),
    DONT_HAVE_PERMISSION("You dont have permission to cancel this booking"),
    BOOKING_WAS_CANCELLED_BY_ID("Booking with id: %s has been CANCELED"),

    TIME_IS_EQUALS("Start time cannot equal end time"),
    END_TIME_CANNOT_BE_START_TIME("End time cannot be before start time"),
    TIME_UNDEFINED("Time is undefined"),
    TIME_FROM_BEFORE_CURRENT_TIME("Time_from is before current time + 15 minutes"),

    UNDEFINED("Undefined"),
    ;

    private final String message;

    public String getMessage(Object... args){
        return String.format(message, args);
    }
}
