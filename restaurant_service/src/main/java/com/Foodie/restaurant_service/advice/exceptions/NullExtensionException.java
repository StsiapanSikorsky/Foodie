package com.Foodie.restaurant_service.advice.exceptions;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NullExtensionException extends RuntimeException {
    public NullExtensionException(String message) {
        super(message);
    }
}
