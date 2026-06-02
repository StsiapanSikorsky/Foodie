package com.Foodie.booking_service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    USER("USER"),
    OWNER("OWNER"),
    ADMIN("ADMIN");

    private final String role;

    public static UserRole fromName(String name){
        return UserRole.valueOf(name.toUpperCase());
    }
}
