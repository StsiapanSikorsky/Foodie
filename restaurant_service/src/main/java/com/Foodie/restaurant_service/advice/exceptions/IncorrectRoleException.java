package com.Foodie.restaurant_service.advice.exceptions;

public class IncorrectRoleException extends RuntimeException {
    public IncorrectRoleException(String message) {
        super(message);
    }
}
