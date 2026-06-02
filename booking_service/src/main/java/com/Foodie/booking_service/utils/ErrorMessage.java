package com.Foodie.booking_service.utils;

import com.Foodie.booking_service.advice.exception.IncorrectRoleException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ErrorMessage {
    BOOKING_CONFLICT("Booking with you time was busy"),
    GUESTS_CONFLICT("Table has %s max guests"),
    BOOKING_NOT_FOUND_BY_ID("Booking with id: %s was not found"),
    INCORRECT_ROLE("You role forbidden for this event. Yours roles: %s"),
    DONT_HAVE_PERMISSION("You dont have permission to cancel this booking");


    private final String message;

    public String getMessage(Object... args){
        return String.format(message, args);
    }
}
