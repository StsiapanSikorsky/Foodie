package com.Foodie.booking_service.response.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRefreshResponse {

    private String token;
    private String refreshToken;
}
