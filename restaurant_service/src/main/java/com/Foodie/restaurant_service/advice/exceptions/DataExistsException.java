package com.Foodie.restaurant_service.advice.exceptions;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataExistsException extends RuntimeException {
    public DataExistsException(String message) {
        super(message);
    }
}
