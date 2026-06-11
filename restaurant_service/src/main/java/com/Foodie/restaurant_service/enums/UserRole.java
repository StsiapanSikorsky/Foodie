package com.Foodie.restaurant_service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    USER("USER"),
    OWNER("OWNER"),
    ADMIN("ADMIN");

    private final String role;
}
