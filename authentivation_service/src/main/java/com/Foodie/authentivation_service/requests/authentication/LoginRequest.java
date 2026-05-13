package com.Foodie.authentivation_service.requests.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
public class LoginRequest implements Serializable {

    @NotBlank(message = "Email has not be empty")
    @Email(message = "Incorrect Email form")
    private String email;

    @NotBlank(message = "Password has not be empty")
    private String password;
}
