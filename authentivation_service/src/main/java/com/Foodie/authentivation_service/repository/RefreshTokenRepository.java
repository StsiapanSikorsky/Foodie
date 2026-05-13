package com.Foodie.authentivation_service.repository;

import com.Foodie.authentivation_service.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByUserId(Integer userId);

    Optional<RefreshToken> findByRefreshToken (String refreshToken);
}
