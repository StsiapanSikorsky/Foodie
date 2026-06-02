package com.Foodie.booking_service.advice.exception;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IncorrectRoleException extends RuntimeException {
    public IncorrectRoleException(String message) {
        super(message);
    }
}
