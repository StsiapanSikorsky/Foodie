package com.Foodie.authentivation_service.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LogMessage {

    TOKEN_CREATED_OR_UPDATED ("Token has be created or updated");

    private final String message;

    public String getMessage(Object ... args) {
        return String.format(message,args);
    }
}
