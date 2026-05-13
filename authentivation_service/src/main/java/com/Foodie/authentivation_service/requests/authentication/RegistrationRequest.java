package com.Foodie.authentivation_service.requests.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RegistrationRequest {

    @NotBlank(message = "User name has not be empty")
    private String  userName;

    @NotBlank(message = "Email has not be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password has not be empty")
    private String password;
}
