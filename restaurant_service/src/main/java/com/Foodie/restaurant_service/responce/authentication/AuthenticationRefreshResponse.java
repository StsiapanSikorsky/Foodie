package com.Foodie.restaurant_service.responce.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRefreshResponse {

    private String token;
    private String refreshToken;
}
