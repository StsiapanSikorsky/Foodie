package com.Foodie.authentivation_service.unit;

import com.Foodie.authentivation_service.advice.exception.DataExistException;
import com.Foodie.authentivation_service.advice.exception.NotFoundException;
import com.Foodie.authentivation_service.dto.UserDto;
import com.Foodie.authentivation_service.entity.User;
import com.Foodie.authentivation_service.mapper.UserMapper;
import com.Foodie.authentivation_service.repository.UserRepository;
import com.Foodie.authentivation_service.requests.owner.UpdateOwnerRequest;
import com.Foodie.authentivation_service.requests.user.UpdateUserRequest;
import com.Foodie.authentivation_service.responce.owner.OwnerResponse;
import com.Foodie.authentivation_service.responce.user.UserResponse;
import com.Foodie.authentivation_service.services.impl.UserServiceImpl;
import com.Foodie.authentivation_service.utils.AccessValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AccessValidator accessValidator;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private User updatedUser;
    private UserDto updatedUserDto;
    private UpdateUserRequest updateUserRequest;

    @BeforeEach()
    void setUp(){
        user = new User();
        user.setId(1);
        user.setEmail("test@gmail.com");
        user.setPassword("pass123");

        userDto = new UserDto();
        userDto.setId(1);
        userDto.setEmail("test@gmail.com");

        updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setEmail("updated_email@gmail.com");

        updatedUserDto = new UserDto();
        updatedUserDto.setId(1);
        updatedUserDto.setEmail("updated_email@gmail.com");

        updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setUserName("Update owner");
        updateUserRequest.setEmail("updated_email@gmail.com");
    }

    @Test
    void getUserById_Success(){
        when(userRepository.getUserByIdAndDeletedFalse(1)).thenReturn(Optional.of(user));
        when(userMapper.userToUserDto(user)).thenReturn(userDto);

        UserResponse<UserDto> result = userService.getUserById(1);

        assertNotNull(result);
        assertEquals(result.getPayload().getId(), user.getId());

        verify(userRepository, times(1)).getUserByIdAndDeletedFalse(1);
        verify(userMapper, times(1)).userToUserDto(user);
    }

    @Test
    void getUserById_UserNotFound_ThrowNotFoundException(){
        when(userRepository.getUserByIdAndDeletedFalse(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");

        verify(userRepository, times(1)).getUserByIdAndDeletedFalse(999);
        verify(userMapper, never()).userToUserDto(user);
    }

    @Test
    void updateUser_Success(){
        when(userRepository.getUserByIdAndDeletedFalse(1)).thenReturn(Optional.of(user));
        when(userRepository.existsByUserName(updateUserRequest.getUserName())).thenReturn(false);
        when(userRepository.existsByEmail(updateUserRequest.getEmail())).thenReturn(false);
        doNothing().when(accessValidator).validateAdminOrOwnerAccess(1);
        when(userMapper.updatedUsertoUser(user, updateUserRequest)).thenReturn(updatedUser);
        when(userMapper.userToUserDto(updatedUser)).thenReturn(updatedUserDto);

        UserResponse<UserDto> result = userService.updateUser(1, updateUserRequest);

        assertNotNull(result);
        assertEquals(result.getPayload().getId(), updatedUser.getId());
        assertEquals(result.getPayload().getEmail(), updatedUser.getEmail());

        verify(userRepository, times(1)).getUserByIdAndDeletedFalse(1);
        verify(userRepository, times(1)).existsByUserName(updateUserRequest.getUserName());
        verify(userRepository, times(1)).existsByEmail(updateUserRequest.getEmail());
        verify(accessValidator, times(1)).validateAdminOrOwnerAccess(1);
        verify(userMapper, times(1)).updatedUsertoUser(user, updateUserRequest);
        verify(userMapper, times(1)).userToUserDto(updatedUser);
    }

    @Test
    void updateUser_NotOwnerOrAdmin_ThrowAccessDeniedException(){
        when(userRepository.getUserByIdAndDeletedFalse(2)).thenReturn(Optional.of(user));
        when(userRepository.existsByUserName(updateUserRequest.getUserName())).thenReturn(false);
        when(userRepository.existsByEmail(updateUserRequest.getEmail())).thenReturn(false);
        doThrow(new AccessDeniedException("dont have permissions")).when(accessValidator).validateAdminOrOwnerAccess(2);

        assertThatThrownBy(() -> userService.updateUser(2, updateUserRequest))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("dont have permissions");

        verify(userRepository, times(1)).getUserByIdAndDeletedFalse(2);
        verify(userRepository, times(1)).existsByUserName(updateUserRequest.getUserName());
        verify(userRepository, times(1)).existsByEmail(updateUserRequest.getEmail());
        verify(accessValidator, times(1)).validateAdminOrOwnerAccess(2);
        verify(userMapper, never()).updatedUsertoUser(user, updateUserRequest);
        verify(userMapper, never()).userToUserDto(updatedUser);
    }

    @Test
    void updateOwner_UsernameAlreadyExist_ThrowDataExistException(){
        when(userRepository.getUserByIdAndDeletedFalse(1)).thenReturn(Optional.of(user));
        when(userRepository.existsByUserName(updateUserRequest.getUserName())).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(1, updateUserRequest))
                .isInstanceOf(DataExistException.class)
                .hasMessageContaining("already exists");

        verify(userRepository, times(1)).getUserByIdAndDeletedFalse(1);
        verify(userRepository, times(1)).existsByUserName(updateUserRequest.getUserName());
        verify(userRepository, never()).existsByEmail(updateUserRequest.getEmail());
        verify(accessValidator, never()).validateAdminOrOwnerAccess(2);
        verify(userMapper, never()).updatedUsertoUser(user, updateUserRequest);
        verify(userMapper, never()).userToUserDto(updatedUser);
    }

    @Test
    void updateOwner_EmailAlreadyExist_ThrowDataExistException(){
        when(userRepository.getUserByIdAndDeletedFalse(1)).thenReturn(Optional.of(user));
        when(userRepository.existsByUserName(updateUserRequest.getUserName())).thenReturn(false);
        when(userRepository.existsByEmail(updateUserRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(1, updateUserRequest))
                .isInstanceOf(DataExistException.class)
                .hasMessageContaining("already exists");

        verify(userRepository, times(1)).getUserByIdAndDeletedFalse(1);
        verify(userRepository, times(1)).existsByUserName(updateUserRequest.getUserName());
        verify(userRepository, times(1)).existsByEmail(updateUserRequest.getEmail());
        verify(accessValidator, never()).validateAdminOrOwnerAccess(2);
        verify(userMapper, never()).updatedUsertoUser(user, updateUserRequest);
        verify(userMapper, never()).userToUserDto(updatedUser);
    }

    @Test
    void softDeleteUser_Success(){
        when(userRepository.getUserByIdAndDeletedFalse(1)).thenReturn(Optional.of(user));
        doNothing().when(accessValidator).validateAdminOrOwnerAccess(1);
        when(userRepository.save(user)).thenReturn(user);

        userService.softDeleteUser(1);

        verify(userRepository, times(1)).getUserByIdAndDeletedFalse(1);
        verify(accessValidator, times(1)).validateAdminOrOwnerAccess(1);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void softDeleteUser_UserNotFound_ThrowNotFoundException(){
        when(userRepository.getUserByIdAndDeletedFalse(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.softDeleteUser(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");

        verify(userRepository, times(1)).getUserByIdAndDeletedFalse(999);
        verify(accessValidator, never()).validateAdminOrOwnerAccess(1);
        verify(userRepository, never()).save(user);
    }

    @Test
    void softDeleteUser_NotOwnerOrAdmin_ThrowAccessDeniedException(){
        when(userRepository.getUserByIdAndDeletedFalse(2)).thenReturn(Optional.of(user));
        doThrow(new AccessDeniedException("dont have permissions")).when(accessValidator).validateAdminOrOwnerAccess(2);

        assertThatThrownBy(() -> userService.softDeleteUser(2))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("dont have permissions");

        verify(userRepository, times(1)).getUserByIdAndDeletedFalse(2);
        verify(accessValidator, times(1)).validateAdminOrOwnerAccess(2);
        verify(userRepository, never()).save(user);
    }
}
