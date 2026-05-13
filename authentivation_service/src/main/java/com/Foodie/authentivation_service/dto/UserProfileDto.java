package com.Foodie.authentivation_service.dto;

import com.Foodie.authentivation_service.entity.RefreshToken;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto implements Serializable {

    Integer id;
    String userName;
    String email;
    LocalDateTime lastLogin;

    String token;
    String refreshToken;

    Set<RoleDto> roles;
}
