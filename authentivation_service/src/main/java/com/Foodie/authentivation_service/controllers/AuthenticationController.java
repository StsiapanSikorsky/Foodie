package com.Foodie.authentivation_service.controllers;

import com.Foodie.authentivation_service.dto.UserProfileDto;
import com.Foodie.authentivation_service.requests.authentication.LoginRequest;
import com.Foodie.authentivation_service.requests.authentication.RegistrationRequest;
import com.Foodie.authentivation_service.responce.authentication.AuthenticationRefreshResponse;
import com.Foodie.authentivation_service.responce.authentication.AuthenticationResponse;
import com.Foodie.authentivation_service.responce.authentication.TokenValidationResponse;
import com.Foodie.authentivation_service.services.AuthenticationService;
import com.Foodie.authentivation_service.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("${end.point.authentication}")
@RequiredArgsConstructor
@Tag(name = "Authorization controller")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("${end.point.user.register}")
    @Operation(
            summary = "Регистрация пользователя",
            description = "Регистрация пользователя с ролью User"
    )
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
    @Operation(
            summary = "Логин пользователя",
            description = "Логин пользователя с ролью User"
    )
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
    @Operation(
            summary = "Регистрация собственника",
            description = "Регистрация пользователя с ролью OWNER"
    )
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
    @Operation(
            summary = "Логин собственника",
            description = "Логин пользователя с ролью OWNER"
    )
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
    @Operation(
            summary = "Проверка валидности JWT токена",
            description = "Проверка валидности JWT токена"
    )
    public ResponseEntity<TokenValidationResponse> validateToken(
            @RequestHeader("Authorization") String authHeader
    ) {
        System.out.println("Вызов метода /validate с: " + authHeader);

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

    @GetMapping("${end.point.refresh.token}")
    @Operation(
            summary = "Обновление JWT токена",
            description = "Обновление JWT токена с проверкой RefreshToken"
    )
    public ResponseEntity<AuthenticationRefreshResponse> refreshToken(
            @RequestHeader(name = "REFRESH_TOKEN") String refreshToken,
            HttpServletResponse response
    )
    {
        AuthenticationRefreshResponse result = authenticationService.refreshAccessToken(refreshToken);

        Cookie authorizationCookie = Utils.createAuthenticationCookie(result.getToken());
        Cookie refreshtokenCookie = Utils.creauteRefreshTokenCookie(result.getRefreshToken());
        response.addCookie(authorizationCookie);
        response.addCookie(refreshtokenCookie);

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }
}
