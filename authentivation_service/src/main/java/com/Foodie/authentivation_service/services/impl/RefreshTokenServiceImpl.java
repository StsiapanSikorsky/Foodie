package com.Foodie.authentivation_service.services.impl;

import com.Foodie.authentivation_service.advice.exception.InvalidDataException;
import com.Foodie.authentivation_service.advice.exception.NotFoundException;
import com.Foodie.authentivation_service.entity.RefreshToken;
import com.Foodie.authentivation_service.entity.User;
import com.Foodie.authentivation_service.enums.ErrorMessage;
import com.Foodie.authentivation_service.repository.RefreshTokenRepository;
import com.Foodie.authentivation_service.services.RefreshTokenService;
import com.Foodie.authentivation_service.utils.UtilsRefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;


    @Override
    public RefreshToken generateOrUpdateRefreshToken(
            User user
    ) {
        return refreshTokenRepository.findByUserId(user.getId())
                .map(refreshToken -> {
                    refreshToken.setCreated(LocalDateTime.now());
                    refreshToken.setRefreshToken(UtilsRefreshToken.generateUuidWithoutDash());
                    refreshToken.setExpiryDate(LocalDateTime.now().plusDays(30));
                    return refreshTokenRepository.save(refreshToken);
                })
                .orElseGet(() -> {
                    RefreshToken newToken = new RefreshToken();
                    newToken.setUser(user);
                    newToken.setCreated(LocalDateTime.now());
                    newToken.setRefreshToken(UtilsRefreshToken.generateUuidWithoutDash());
                    newToken.setExpiryDate(LocalDateTime.now().plusDays(30));
                    return refreshTokenRepository.save(newToken);
                });
    }

    @Override
    public RefreshToken validateAndRefreshRefreshToken(
            String requestRefreshToken
    ) {
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(requestRefreshToken)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_FOUND_REFRESH_TOKEN.getMessage()));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidDataException(ErrorMessage.REFRESH_TOKEN_HAS_EXPIRED.getMessage());
        }

        refreshToken.setCreated(LocalDateTime.now());
        refreshToken.setRefreshToken(UtilsRefreshToken.generateUuidWithoutDash());
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(30));

        return refreshTokenRepository.save(refreshToken);
    }
}
