package com.Foodie.authentivation_service.services;

import com.Foodie.authentivation_service.dto.UserProfileDto;
import com.Foodie.authentivation_service.requests.authentication.LoginRequest;
import com.Foodie.authentivation_service.requests.authentication.RegistrationRequest;
import com.Foodie.authentivation_service.responce.authentication.AuthenticationRefreshResponse;
import com.Foodie.authentivation_service.responce.authentication.AuthenticationResponse;
import com.Foodie.authentivation_service.responce.authentication.TokenValidationResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


public interface AuthenticationService{

    AuthenticationResponse<UserProfileDto> registerUser(@NotNull @Valid RegistrationRequest request);

    AuthenticationResponse<UserProfileDto> loginUser(@NotNull @Valid LoginRequest request);

    AuthenticationResponse<UserProfileDto> registerOwner(@NotNull @Valid RegistrationRequest request);

    AuthenticationResponse<UserProfileDto> loginOwner(@NotNull @Valid LoginRequest request);

    TokenValidationResponse validateToken(@NotNull String token);

    AuthenticationRefreshResponse refreshAccessToken(@NotNull String refreshToken);
}
