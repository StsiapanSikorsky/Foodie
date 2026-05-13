package com.Foodie.authentivation_service.mapper;

import com.Foodie.authentivation_service.dto.RoleDto;
import com.Foodie.authentivation_service.dto.UserDto;
import com.Foodie.authentivation_service.dto.UserProfileDto;
import com.Foodie.authentivation_service.entity.Role;
import com.Foodie.authentivation_service.entity.User;
import com.Foodie.authentivation_service.requests.authentication.RegistrationRequest;
import com.Foodie.authentivation_service.requests.owner.UpdateOwnerRequest;
import com.Foodie.authentivation_service.requests.user.UpdateUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
    componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    User registrationRequestToUser(RegistrationRequest request);


    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    @Mapping(target = "userName", source = "user.userName")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "lastLogin", source = "user.lastLogin")
    @Mapping(target = "token", source = "jwtToken")
    @Mapping(target = "refreshToken", source = "refreshToken")
    UserProfileDto userToUserProfileDto(User user, String jwtToken, String refreshToken);


    UserDto userToUserDto(User user);

    @Mapping(target = "userName", source = "userName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    User updatedUsertoUser(@MappingTarget User user, UpdateUserRequest updateUserRequest);

    @Mapping(target = "userName", source = "userName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    User updatedOwnertoUser(@MappingTarget User user, UpdateOwnerRequest updateUserRequest);

    default Set<RoleDto> mapRoles(Collection<Role> roles){
        return roles.stream()
                .map(role -> new RoleDto(role.getId(), role.getName()))
                .collect(Collectors.toSet());
    }
}
