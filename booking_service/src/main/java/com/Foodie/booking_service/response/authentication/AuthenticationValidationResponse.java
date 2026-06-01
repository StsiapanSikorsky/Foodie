package com.Foodie.booking_service.response.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
