package com.Foodie.authentivation_service.services;

import com.Foodie.authentivation_service.entity.RefreshToken;
import com.Foodie.authentivation_service.entity.User;

public interface RefreshTokenService {

    RefreshToken generateOrUpdateRefreshToken(User user);

    RefreshToken validateAndRefreshRefreshToken(String refreshToken);
}
