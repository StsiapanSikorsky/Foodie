package com.Foodie.authentivation_service.services;

import com.Foodie.authentivation_service.dto.UserDto;
import com.Foodie.authentivation_service.requests.user.UpdateUserRequest;
import com.Foodie.authentivation_service.responce.user.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserResponse<UserDto> getUserById(Integer id);

    UserResponse<UserDto> updateUser(Integer id, UpdateUserRequest request);

    void softDeleteUser(Integer id);
}
