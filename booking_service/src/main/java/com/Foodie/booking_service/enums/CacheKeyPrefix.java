package com.Foodie.booking_service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CacheKeyPrefix {
    BOOKING("booking:"),
    BOOKING_WITH_PAGINATION("bookings:user:%d:page:%d:size:%d");

    private final String prefix;
}
