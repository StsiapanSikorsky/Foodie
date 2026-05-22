package com.Foodie.authentivation_service.services.impl;

import com.Foodie.authentivation_service.advice.exception.DataExistException;
import com.Foodie.authentivation_service.advice.exception.NotFoundException;
import com.Foodie.authentivation_service.dto.UserDto;
import com.Foodie.authentivation_service.entity.User;
import com.Foodie.authentivation_service.enums.ErrorMessage;
import com.Foodie.authentivation_service.mapper.UserMapper;
import com.Foodie.authentivation_service.repository.UserRepository;
import com.Foodie.authentivation_service.requests.user.UpdateUserRequest;
import com.Foodie.authentivation_service.responce.user.UserResponse;
import com.Foodie.authentivation_service.services.UserService;
import com.Foodie.authentivation_service.utils.AccessValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AccessValidator accessValidator;

    @Override
    public UserResponse<UserDto> getUserById(
            @NotNull Integer id
    ) {
        User user = userRepository.getUserByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(id)));

        UserDto userDto = userMapper.userToUserDto(user);
        return UserResponse.createSuccessful(userDto);
    }

    @Override
    @Transactional
    public UserResponse<UserDto> updateUser(
            @NotNull Integer id,
            @Valid UpdateUserRequest request
    ) {
        User user = userRepository.getUserByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(id)));

        if(userRepository.existsByUserName(request.getUserName())){
            throw new DataExistException(ErrorMessage.USERNAME_ALREADY_EXISTS.getMessage(request.getUserName()));
        }
        if(userRepository.existsByEmail(request.getEmail())){
            throw new DataExistException(ErrorMessage.USER_EMAIL_ALREADY_EXISTS.getMessage(request.getEmail()));
        }

        accessValidator.validateAdminOrOwnerAccess(id);

        User updatedUser = userMapper.updatedUsertoUser(user, request);
        updatedUser.setUpdated(LocalDateTime.now());
        userRepository.save(updatedUser);

        UserDto userDto = userMapper.userToUserDto(updatedUser);
        return UserResponse.createSuccessful(userDto);
    }

    @Override
    @Transactional
    public void softDeleteUser(
            @NotNull Integer id
    ) {
        User user = userRepository.getUserByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(id)));

        accessValidator.validateAdminOrOwnerAccess(id);

        user.setDeleted(true);
        userRepository.save(user);
    }


    @Override
    public UserDetails loadUserByUsername(
            String email
    ) throws UsernameNotFoundException
    {
        return getUserDetails(email, userRepository);
    }

    static UserDetails getUserDetails(
            String email,
            UserRepository userRepository
    ) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL.getMessage(email)));

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
        );
    }
}
