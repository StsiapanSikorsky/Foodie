package com.Foodie.authentivation_service.controllers;

import com.Foodie.authentivation_service.dto.UserProfileDto;
import com.Foodie.authentivation_service.requests.authentication.LoginRequest;
import com.Foodie.authentivation_service.requests.authentication.RegistrationRequest;
import com.Foodie.authentivation_service.responce.authentication.AuthenticationResponse;
import com.Foodie.authentivation_service.responce.authentication.TokenValidationResponse;
import com.Foodie.authentivation_service.services.AuthenticationService;
import com.Foodie.authentivation_service.utils.Utils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("${end.point.authentication}")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("${end.point.user.register}")
    public ResponseEntity<AuthenticationResponse<UserProfileDto>> registerUser(
            @RequestBody @Valid RegistrationRequest request,
            HttpServletResponse response
    ){
        AuthenticationResponse<UserProfileDto> result = authenticationService.registerUser(request);

        Cookie authorizationCookie = Utils.createAuthenticationCookie(result.getPayload().getToken());
        Cookie refreshtokenCookie = Utils.creauteRefreshTokenCookie(result.getPayload().getRefreshToken());
        response.addCookie(authorizationCookie);
        response.addCookie(refreshtokenCookie);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(result);
    }

    @PostMapping("${end.point.user.login}")
    public ResponseEntity<AuthenticationResponse<UserProfileDto>> loginUser(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse response
    ){
        AuthenticationResponse<UserProfileDto> result = authenticationService.loginUser(request);

        Cookie authorizationCookie = Utils.createAuthenticationCookie(result.getPayload().getToken());
        Cookie refreshtokenCookie = Utils.creauteRefreshTokenCookie(result.getPayload().getRefreshToken());
        response.addCookie(authorizationCookie);
        response.addCookie(refreshtokenCookie);

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    @PostMapping("${end.point.owner.register}")
    public ResponseEntity<AuthenticationResponse<UserProfileDto>> registerOwner(
            @RequestBody @Valid RegistrationRequest request,
            HttpServletResponse response
    ){
        AuthenticationResponse<UserProfileDto> result = authenticationService.registerOwner(request);

        Cookie authorizationCookie = Utils.createAuthenticationCookie(result.getPayload().getToken());
        Cookie refreshtokenCookie = Utils.creauteRefreshTokenCookie(result.getPayload().getRefreshToken());
        response.addCookie(authorizationCookie);
        response.addCookie(refreshtokenCookie);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(result);
    }

    @PostMapping("${end.point.owner.login}")
    public ResponseEntity<AuthenticationResponse<UserProfileDto>> loginOwner(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse response
    ){
        AuthenticationResponse<UserProfileDto> result = authenticationService.loginOwner(request);

        Cookie authorizationCookie = Utils.createAuthenticationCookie(result.getPayload().getToken());
        Cookie refreshtokenCookie = Utils.creauteRefreshTokenCookie(result.getPayload().getRefreshToken());
        response.addCookie(authorizationCookie);
        response.addCookie(refreshtokenCookie);

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("${end.point.validate}")
    public ResponseEntity<TokenValidationResponse> validateToken(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader != null && authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : null;

        if (token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new TokenValidationResponse(false, null, null, null));
        }

        TokenValidationResponse response = authenticationService.validateToken(token);

        return ResponseEntity.ok(response);
    }


    //TODO:Проверить через API Gateway при 401
    @PostMapping("${end.point.refresh.token}")
    public ResponseEntity<AuthenticationResponse<UserProfileDto>> refreshToken(
            @RequestParam(name = "token") String refreshToken,
            HttpServletResponse response,
            Authentication authentication)
    {
        AuthenticationResponse<UserProfileDto> result = authenticationService.refreshAccessToken(refreshToken);

        Cookie authorizationCookie = Utils.createAuthenticationCookie(result.getPayload().getToken());
        Cookie refreshtokenCookie = Utils.creauteRefreshTokenCookie(result.getPayload().getRefreshToken());
        response.addCookie(authorizationCookie);
        response.addCookie(refreshtokenCookie);

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }
}
