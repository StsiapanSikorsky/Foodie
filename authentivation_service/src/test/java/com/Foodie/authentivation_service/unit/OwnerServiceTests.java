package com.Foodie.authentivation_service.unit;

import com.Foodie.authentivation_service.advice.exception.DataExistException;
import com.Foodie.authentivation_service.advice.exception.NotFoundException;
import com.Foodie.authentivation_service.dto.UserDto;
import com.Foodie.authentivation_service.entity.User;
import com.Foodie.authentivation_service.enums.ErrorMessage;
import com.Foodie.authentivation_service.mapper.UserMapper;
import com.Foodie.authentivation_service.repository.UserRepository;
import com.Foodie.authentivation_service.requests.owner.UpdateOwnerRequest;
import com.Foodie.authentivation_service.responce.owner.OwnerResponse;
import com.Foodie.authentivation_service.services.impl.OwnerServiceImpl;
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
public class OwnerServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AccessValidator accessValidator;

    @InjectMocks
    private OwnerServiceImpl ownerService;

    private User owner;
    private UserDto ownerDto;
    private User updatedOwner;
    private UserDto updatedOwnerDto;
    private UpdateOwnerRequest updateOwnerRequest;


    @BeforeEach()
    void setUp() {
        owner = new User();
        owner.setId(1);
        owner.setEmail("test@gmail.com");
        owner.setPassword("pass123");

        ownerDto = new UserDto();
        ownerDto.setId(1);
        ownerDto.setEmail("test@gmail.com");

        updatedOwner = new User();
        updatedOwner.setId(1);
        updatedOwner.setEmail("updated_email@gmail.com");

        updatedOwnerDto = new UserDto();
        updatedOwnerDto.setId(1);
        updatedOwnerDto.setEmail("updated_email@gmail.com");

        updateOwnerRequest = new UpdateOwnerRequest();
        updateOwnerRequest.setUserName("Update owner");
        updateOwnerRequest.setEmail("updated_email@gmail.com");
    }

    @Test
    void getOwnerById_Success(){
        when(userRepository.getUserByIdAndDeletedFalse(1)).thenReturn(Optional.of(owner));
        when(userMapper.userToUserDto(owner)).thenReturn(ownerDto);

        OwnerResponse<UserDto> result = ownerService.getOwnerById(1);

        assertNotNull(result);
        assertEquals(result.getPayload().getId(), owner.getId());

        verify(userRepository, times(1)).getUserByIdAndDeletedFalse(1);
        verify(userMapper, times(1)).userToUserDto(owner);
    }

    @Test
    void getOwnerById_OwnerNotFound_ThrowNotFoundException(){
        when(userRepository.getUserByIdAndDeletedFalse(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ownerService.getOwnerById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");

        verify(userRepository, times(1)).getUserByIdAndDeletedFalse(999);
        verify(userMapper, never()).userToUserDto(owner);
    }

    @Test
    void updateOwner_Success(){
        when(userRepository.getUserByIdAndDeletedFalse(1)).thenReturn(Optional.of(owner));
        when(userRepository.existsByUserName(updateOwnerRequest.getUserName())).thenReturn(false);
        when(userRepository.existsByEmail(updateOwnerRequest.getEmail())).thenReturn(false);
        doNothing().when(accessValidator).validateAdminOrOwnerAccess(1);
        when(userMapper.updatedOwnertoUser(owner, updateOwnerRequest)).thenReturn(updatedOwner);
        when(userMapper.userToUserDto(updatedOwner)).thenReturn(updatedOwnerDto);

        OwnerResponse<UserDto> result = ownerService.updateOwner(1, updateOwnerRequest);

        assertNotNull(result);
        assertEquals(result.getPayload().getId(), updatedOwner.getId());
        assertEquals(result.getPayload().getEmail(), updatedOwner.getEmail());

        verify(userRepository, times(1)).getUserByIdAndDeletedFalse(1);
        verify(userRepository, times(1)).existsByUserName(updateOwnerRequest.getUserName());
        verify(userRepository, times(1)).existsByEmail(updateOwnerRequest.getEmail());
        verify(accessValidator, times(1)).validateAdminOrOwnerAccess(1);
        verify(userMapper, times(1)).updatedOwnertoUser(owner, updateOwnerRequest);
        verify(userMapper, times(1)).userToUserDto(updatedOwner);
    }

    @Test
    void updateOwner_NotOwnerOrAdmin_ThrowAccessDeniedException(){
        when(userRepository.getUserByIdAndDeletedFalse(2)).thenReturn(Optional.of(owner));
        when(userRepository.existsByUserName(updateOwnerRequest.getUserName())).thenReturn(false);
        when(userRepository.existsByEmail(updateOwnerRequest.getEmail())).thenReturn(false);
        doThrow(new AccessDeniedException("dont have permissions")).when(accessValidator).validateAdminOrOwnerAccess(2);

        assertThatThrownBy(() -> ownerService.updateOwner(2, updateOwnerRequest))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("dont have permissions");

        verify(userRepository, times(1)).getUserByIdAndDeletedFalse(2);
        verify(userRepository, times(1)).existsByUserName(updateOwnerRequest.getUserName());
        verify(userRepository, times(1)).existsByEmail(updateOwnerRequest.getEmail());
        verify(accessValidator, times(1)).validateAdminOrOwnerAccess(2);
        verify(userMapper, never()).updatedOwnertoUser(owner, updateOwnerRequest);
        verify(userMapper, never()).userToUserDto(updatedOwner);
    }

    @Test
    void updateOwner_UsernameAlreadyExist_ThrowDataExistException(){
        when(userRepository.getUserByIdAndDeletedFalse(1)).thenReturn(Optional.of(owner));
        when(userRepository.existsByUserName(updateOwnerRequest.getUserName())).thenReturn(true);

        assertThatThrownBy(() -> ownerService.updateOwner(1, updateOwnerRequest))
                .isInstanceOf(DataExistException.class)
                .hasMessageContaining("already exists");

        verify(userRepository, times(1)).getUserByIdAndDeletedFalse(1);
        verify(userRepository, times(1)).existsByUserName(updateOwnerRequest.getUserName());
        verify(userRepository, never()).existsByEmail(updateOwnerRequest.getEmail());
        verify(accessValidator, never()).validateAdminOrOwnerAccess(2);
        verify(userMapper, never()).updatedOwnertoUser(owner, updateOwnerRequest);
        verify(userMapper, never()).userToUserDto(updatedOwner);
    }

    @Test
    void updateOwner_EmailAlreadyExist_ThrowDataExistException(){
        when(userRepository.getUserByIdAndDeletedFalse(1)).thenReturn(Optional.of(owner));
        when(userRepository.existsByUserName(updateOwnerRequest.getUserName())).thenReturn(false);
        when(userRepository.existsByEmail(updateOwnerRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> ownerService.updateOwner(1, updateOwnerRequest))
                .isInstanceOf(DataExistException.class)
                .hasMessageContaining("already exists");

        verify(userRepository, times(1)).getUserByIdAndDeletedFalse(1);
        verify(userRepository, times(1)).existsByUserName(updateOwnerRequest.getUserName());
        verify(userRepository, times(1)).existsByEmail(updateOwnerRequest.getEmail());
        verify(accessValidator, never()).validateAdminOrOwnerAccess(2);
        verify(userMapper, never()).updatedOwnertoUser(owner, updateOwnerRequest);
        verify(userMapper, never()).userToUserDto(updatedOwner);
    }

    @Test
    void softDeleteOwner_Success(){
        when(userRepository.getUserByIdAndDeletedFalse(1)).thenReturn(Optional.of(owner));
        doNothing().when(accessValidator).validateAdminOrOwnerAccess(1);
        when(userRepository.save(owner)).thenReturn(owner);

        ownerService.softDeleteOwner(1);

        verify(userRepository, times(1)).getUserByIdAndDeletedFalse(1);
        verify(accessValidator, times(1)).validateAdminOrOwnerAccess(1);
        verify(userRepository, times(1)).save(owner);
    }

    @Test
    void softDeleteOwner_UserNotFound_ThrowNotFoundException(){
        when(userRepository.getUserByIdAndDeletedFalse(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ownerService.softDeleteOwner(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");

        verify(userRepository, times(1)).getUserByIdAndDeletedFalse(999);
        verify(accessValidator, never()).validateAdminOrOwnerAccess(1);
        verify(userRepository, never()).save(owner);
    }

    @Test
    void softDeleteOwner_NotOwnerOrAdmin_ThrowAccessDeniedException(){
        when(userRepository.getUserByIdAndDeletedFalse(2)).thenReturn(Optional.of(owner));
        doThrow(new AccessDeniedException("dont have permissions")).when(accessValidator).validateAdminOrOwnerAccess(2);

        assertThatThrownBy(() -> ownerService.softDeleteOwner(2))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("dont have permissions");

        verify(userRepository, times(1)).getUserByIdAndDeletedFalse(2);
        verify(accessValidator, times(1)).validateAdminOrOwnerAccess(2);
        verify(userRepository, never()).save(owner);
    }
}
