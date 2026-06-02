package com.Foodie.booking_service.utils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ErrorMessage {
    BOOKING_CONFLICT("Booking with you time was busy"),
    GUESTS_CONFLICT("Table has %s max guests");


    private final String message;

    public String getMessage(Object... args){
        return String.format(message, args);
    }
}
