package com.Foodie.authentivation_service.services.impl;

import com.Foodie.authentivation_service.advice.exception.DataExistException;
import com.Foodie.authentivation_service.advice.exception.NotFoundException;
import com.Foodie.authentivation_service.dto.UserDto;
import com.Foodie.authentivation_service.entity.User;
import com.Foodie.authentivation_service.enums.ErrorMessage;
import com.Foodie.authentivation_service.mapper.UserMapper;
import com.Foodie.authentivation_service.repository.UserRepository;
import com.Foodie.authentivation_service.requests.owner.UpdateOwnerRequest;
import com.Foodie.authentivation_service.responce.owner.OwnerResponse;
import com.Foodie.authentivation_service.services.OwnerService;
import com.Foodie.authentivation_service.utils.AccessValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AccessValidator accessValidator;

    @Override
    public OwnerResponse<UserDto> getOwnerById(
            @NotNull Integer id
    ) {
        User owner = userRepository.getUserByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.OWNER_NOT_FOUND_BY_ID.getMessage(id)));

        UserDto ownerDto = userMapper.userToUserDto(owner);
        return OwnerResponse.createSuccessful(ownerDto);
    }

    @Override
    @Transactional
    public OwnerResponse<UserDto> updateOwner(
            @NotNull Integer id,
            @Valid UpdateOwnerRequest request
    ) {
        User owner = userRepository.getUserByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.OWNER_NOT_FOUND_BY_ID.getMessage(id)));

        if(userRepository.existsByUserName(request.getUserName())){
            throw new DataExistException(ErrorMessage.USERNAME_ALREADY_EXISTS.getMessage(request.getUserName()));
        }
        if(userRepository.existsByEmail(request.getEmail())){
            throw new DataExistException(ErrorMessage.USER_EMAIL_ALREADY_EXISTS.getMessage(request.getEmail()));
        }

        accessValidator.validateAdminOrOwnerAccess(id);

        User updatedOwner = userMapper.updatedOwnertoUser(owner, request);
        updatedOwner.setUpdated(LocalDateTime.now());
        UserDto ownerDto = userMapper.userToUserDto(updatedOwner);

        return OwnerResponse.createSuccessful(ownerDto);
    }

    @Override
    @Transactional
    public void softDeleteOwner(
            @NotNull Integer id
    ) {
        User owner = userRepository.getUserByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.OWNER_NOT_FOUND_BY_ID.getMessage(id)));

        accessValidator.validateAdminOrOwnerAccess(id);

        owner.setDeleted(true);
        userRepository.save(owner);
    }
}
