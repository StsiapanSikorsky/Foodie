package com.Foodie.authentivation_service.services.impl;

import com.Foodie.authentivation_service.advice.exception.InvalidDataException;
import com.Foodie.authentivation_service.advice.exception.NotFoundException;
import com.Foodie.authentivation_service.dto.UserProfileDto;
import com.Foodie.authentivation_service.entity.RefreshToken;
import com.Foodie.authentivation_service.entity.Role;
import com.Foodie.authentivation_service.entity.User;
import com.Foodie.authentivation_service.enums.ErrorMessage;
import com.Foodie.authentivation_service.enums.UserRole;
import com.Foodie.authentivation_service.mapper.UserMapper;
import com.Foodie.authentivation_service.repository.RoleRepository;
import com.Foodie.authentivation_service.repository.UserRepository;
import com.Foodie.authentivation_service.requests.authentication.LoginRequest;
import com.Foodie.authentivation_service.requests.authentication.RegistrationRequest;
import com.Foodie.authentivation_service.responce.authentication.AuthenticationResponse;
import com.Foodie.authentivation_service.responce.authentication.TokenValidationResponse;
import com.Foodie.authentivation_service.security.JwtTokenService;
import com.Foodie.authentivation_service.services.AuthenticationService;
import com.Foodie.authentivation_service.services.RefreshTokenService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;


    @Override
    public AuthenticationResponse<UserProfileDto> registerUser(
            @Valid @NotNull RegistrationRequest request
    ) {

        //TODO:Добавить валидацию пароля
        //TODO:Добавить рефреш токен

        Role userRole = roleRepository.findByName(UserRole.USER.getRole())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ROLE_NOT_FOUND.getMessage()));

        User newUser = userMapper.registrationRequestToUser(request);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        newUser.setRoles(roles);
        newUser.setLastLogin(LocalDateTime.now());

        userRepository.save(newUser);

        String jwtToken = jwtTokenService.generateToken(newUser);
        RefreshToken refreshToken = refreshTokenService.generateOrUpdateRefreshToken(newUser);

        UserProfileDto userProfileDto = userMapper.userToUserProfileDto(newUser, jwtToken, refreshToken.getRefreshToken());

        return AuthenticationResponse.createSuccessfulWithNewToken(userProfileDto);
    }

    @Override
    public AuthenticationResponse<UserProfileDto> loginUser(
            @Valid @NotNull LoginRequest request
    ) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        }
        catch (BadCredentialsException e){
            throw new InvalidDataException(ErrorMessage.INVALID_USER_OR_PASSWORD.getMessage());
        }

        User user = userRepository.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL.getMessage(request.getEmail())));

        String jwtToken = jwtTokenService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.generateOrUpdateRefreshToken(user);

        UserProfileDto userProfileDto = userMapper.userToUserProfileDto(user, jwtToken, refreshToken.getRefreshToken());

        return AuthenticationResponse.createSuccessfulWithNewToken(userProfileDto);
    }

    @Override
    public AuthenticationResponse<UserProfileDto> registerOwner(
            @Valid @NotNull RegistrationRequest request
    ) {
        Role role = roleRepository.findByName(UserRole.OWNER.getRole())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ROLE_NOT_FOUND.getMessage()));

        User newOwner = userMapper.registrationRequestToUser(request);
        newOwner.setPassword(passwordEncoder.encode(request.getPassword()));

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        newOwner.setRoles(roles);
        newOwner.setLastLogin(LocalDateTime.now());

        userRepository.save(newOwner);

        String jwtToken = jwtTokenService.generateToken(newOwner);
        RefreshToken refreshToken = refreshTokenService.generateOrUpdateRefreshToken(newOwner);

        UserProfileDto ownerProfileDto = userMapper.userToUserProfileDto(newOwner, jwtToken, refreshToken.getRefreshToken());

        return AuthenticationResponse.createSuccessfulWithNewToken(ownerProfileDto);
    }

    @Override
    public AuthenticationResponse<UserProfileDto> loginOwner(
            @Valid @NotNull LoginRequest request
    ) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        }
        catch (BadCredentialsException e){
            throw new InvalidDataException(ErrorMessage.INVALID_USER_OR_PASSWORD.getMessage());
        }

        User owner = userRepository.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL.getMessage(request.getEmail())));

        String jwtToken = jwtTokenService.generateToken(owner);
        RefreshToken refreshToken = refreshTokenService.generateOrUpdateRefreshToken(owner);

        UserProfileDto ownerProfileDto = userMapper.userToUserProfileDto(owner, jwtToken, refreshToken.getRefreshToken());

        return AuthenticationResponse.createSuccessfulWithNewToken(ownerProfileDto);
    }

    @Override
    public TokenValidationResponse validateToken(
            @NotNull String token
    ) {
        String email = jwtTokenService.getEmail(token);

        if (email == null || !jwtTokenService.validateToken(token)) {
            return new TokenValidationResponse(
                    false,
                    null,
                    null,
                    null);
        }

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL.getMessage(email)));
        List<String> roles = jwtTokenService.getRoles(token);

        if (user == null) {
            return new TokenValidationResponse(
                    false,
                    null,
                    null,
                    null);
        }

        return new TokenValidationResponse(
                true,
                user.getId(),
                user.getEmail(),
                roles
        );
    }

    @Override
    public AuthenticationResponse<UserProfileDto> refreshAccessToken(
            @NotNull String refreshTokenValue
    ) {
        RefreshToken refreshToken = refreshTokenService.validateAndRefreshRefreshToken(refreshTokenValue);
        User user = refreshToken.getUser();

        String jwtToken = jwtTokenService.generateToken(user);

        UserProfileDto userProfileDto = userMapper.userToUserProfileDto(user, jwtToken, refreshTokenValue);

        return AuthenticationResponse.createSuccessfulWithNewToken(userProfileDto);
    }
}
