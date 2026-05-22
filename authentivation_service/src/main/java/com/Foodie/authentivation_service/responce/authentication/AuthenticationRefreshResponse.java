package com.Foodie.authentivation_service.responce.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRefreshResponse implements Serializable {

    private String token;
    private String refreshToken;
}
