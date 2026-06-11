package com.Foodie.authentivation_service.services.impl;

import com.Foodie.authentivation_service.advice.exception.InvalidDataException;
import com.Foodie.authentivation_service.advice.exception.NotFoundException;
import com.Foodie.authentivation_service.dto.UserProfileDto;
import com.Foodie.authentivation_service.entity.RefreshToken;
import com.Foodie.authentivation_service.entity.Role;
import com.Foodie.authentivation_service.entity.User;
import com.Foodie.authentivation_service.enums.ErrorMessage;
import com.Foodie.authentivation_service.enums.LogMessage;
import com.Foodie.authentivation_service.enums.UserRole;
import com.Foodie.authentivation_service.mapper.UserMapper;
import com.Foodie.authentivation_service.repository.RoleRepository;
import com.Foodie.authentivation_service.repository.UserRepository;
import com.Foodie.authentivation_service.requests.authentication.LoginRequest;
import com.Foodie.authentivation_service.requests.authentication.RegistrationRequest;
import com.Foodie.authentivation_service.responce.authentication.AuthenticationRefreshResponse;
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
        Role userRole = roleRepository.findByName(UserRole.USER.getRole())
                .orElseThrow(() -> {
                    log.warn(ErrorMessage.ROLE_NOT_FOUND.getMessage());
                    return new NotFoundException(ErrorMessage.ROLE_NOT_FOUND.getMessage());
                });

        if(userRepository.existsByEmail(request.getEmail()))
        {
            log.warn(ErrorMessage.USER_EMAIL_ALREADY_EXISTS.getMessage(request.getEmail()));
            throw new InvalidDataException(ErrorMessage.USER_EMAIL_ALREADY_EXISTS.getMessage(request.getEmail()));
        }

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

        log.info(LogMessage.REGISTER_USER_SUCCESS.getMessage(userProfileDto.getId()));
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
            log.warn(ErrorMessage.INVALID_USER_OR_PASSWORD.getMessage());
            throw new InvalidDataException(ErrorMessage.INVALID_USER_OR_PASSWORD.getMessage());
        }

        User user = userRepository.findUserByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn(ErrorMessage.USER_NOT_FOUND_BY_EMAIL.getMessage(request.getEmail()));
                    return new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL.getMessage(request.getEmail()));
                });

        String jwtToken = jwtTokenService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.generateOrUpdateRefreshToken(user);

        UserProfileDto userProfileDto = userMapper.userToUserProfileDto(user, jwtToken, refreshToken.getRefreshToken());

        log.info(LogMessage.LOGIN_USER_SUCCESS.getMessage(userProfileDto.getId()));
        return AuthenticationResponse.createSuccessfulWithNewToken(userProfileDto);
    }

    @Override
    public AuthenticationResponse<UserProfileDto> registerOwner(
            @Valid @NotNull RegistrationRequest request
    ) {
        Role role = roleRepository.findByName(UserRole.OWNER.getRole())
                .orElseThrow(() -> {
                    log.warn(ErrorMessage.ROLE_NOT_FOUND.getMessage());
                    return new NotFoundException(ErrorMessage.ROLE_NOT_FOUND.getMessage());
                });

        if(userRepository.existsByEmail(request.getEmail()))
        {
            log.warn(ErrorMessage.USER_EMAIL_ALREADY_EXISTS.getMessage(request.getEmail()));
            throw new InvalidDataException(ErrorMessage.USER_EMAIL_ALREADY_EXISTS.getMessage(request.getEmail()));
        }

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

        log.info(LogMessage.REGISTER_OWNER_SUCCESS.getMessage(ownerProfileDto.getId()));
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
            log.warn(ErrorMessage.INVALID_USER_OR_PASSWORD.getMessage());
            throw new InvalidDataException(ErrorMessage.INVALID_USER_OR_PASSWORD.getMessage());
        }

        User owner = userRepository.findUserByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn(ErrorMessage.USER_NOT_FOUND_BY_EMAIL.getMessage(request.getEmail()));
                    return new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL.getMessage(request.getEmail()));
                });

        String jwtToken = jwtTokenService.generateToken(owner);
        RefreshToken refreshToken = refreshTokenService.generateOrUpdateRefreshToken(owner);

        UserProfileDto ownerProfileDto = userMapper.userToUserProfileDto(owner, jwtToken, refreshToken.getRefreshToken());

        log.info(LogMessage.LOGIN_OWNER_SUCCESS.getMessage(owner.getId()));
        return AuthenticationResponse.createSuccessfulWithNewToken(ownerProfileDto);
    }

    @Override
    public TokenValidationResponse validateToken(
            @NotNull String token
    ) {
        String email = jwtTokenService.getEmail(token);

        if (email == null || !jwtTokenService.validateToken(token))
        {
            log.info(LogMessage.JWT_TOKEN_NOT_VALID.getMessage());
            return new TokenValidationResponse(false, null, null, null);
        }

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() ->{
                    log.warn(ErrorMessage.USER_NOT_FOUND_BY_EMAIL.getMessage(email));
                    return new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL.getMessage(email));
                });


        List<String> roles = jwtTokenService.getRoles(token);

        log.info(LogMessage.JWT_TOKEN_IS_VALID.getMessage());
        return new TokenValidationResponse(true, user.getId(), user.getEmail(), roles);
    }

    @Override
    public AuthenticationRefreshResponse refreshAccessToken(
            @NotNull String refreshTokenValue
    ) {
        RefreshToken refreshToken = refreshTokenService.validateAndRefreshRefreshToken(refreshTokenValue);

        if (refreshToken == null || refreshToken.getUser() == null) {
            log.warn(ErrorMessage.INVALID_REFRESH_TOKEN.getMessage());
            throw new InvalidDataException(ErrorMessage.INVALID_REFRESH_TOKEN.getMessage());
        }

        User user = refreshToken.getUser();
        String jwtToken = jwtTokenService.generateToken(user);

        return new AuthenticationRefreshResponse(jwtToken, refreshToken.getRefreshToken());
    }
}
