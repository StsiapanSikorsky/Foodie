package com.Foodie.restaurant_service.responce.authentication;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationValidationResponse {

    private boolean valid;
    private Integer userId;
    private String email;
    private List<String> roles;
}
