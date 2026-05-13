package com.Foodie.authentivation_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Serializable {
    Integer id;
    String userName;
    String email;
    LocalDateTime lastLogin;
    Set<RoleDto> roles;
}