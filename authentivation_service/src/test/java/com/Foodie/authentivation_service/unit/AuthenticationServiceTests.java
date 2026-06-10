package com.Foodie.authentivation_service.unit;

import com.Foodie.authentivation_service.advice.exception.InvalidDataException;
import com.Foodie.authentivation_service.advice.exception.NotFoundException;
import com.Foodie.authentivation_service.dto.UserProfileDto;
import com.Foodie.authentivation_service.entity.RefreshToken;
import com.Foodie.authentivation_service.entity.Role;
import com.Foodie.authentivation_service.entity.User;
import com.Foodie.authentivation_service.enums.UserRole;
import com.Foodie.authentivation_service.mapper.UserMapper;
import com.Foodie.authentivation_service.repository.RoleRepository;
import com.Foodie.authentivation_service.repository.UserRepository;
import com.Foodie.authentivation_service.requests.authentication.LoginRequest;
import com.Foodie.authentivation_service.requests.authentication.RegistrationRequest;
import com.Foodie.authentivation_service.responce.authentication.AuthenticationRefreshResponse;
import com.Foodie.authentivation_service.responce.authentication.AuthenticationResponse;
import com.Foodie.authentivation_service.responce.authentication.TokenValidationResponse;
import com.Foodie.authentivation_service.security.JwtTokenService;
import com.Foodie.authentivation_service.services.RefreshTokenService;
import com.Foodie.authentivation_service.services.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class AuthenticationServiceTests {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private User user;
    private User owner;
    private Set<Role> userRoles;
    private Set<Role> ownerRoles;
    private UserProfileDto userProfileDto;
    private UserProfileDto ownerProfileDto;
    private Role userRole;
    private Role ownerRole;
    private RegistrationRequest registrationRequest;
    private LoginRequest loginRequest;

    private String jwtToken;
    private String refreshToken;
    private RefreshToken refreshTokenEntity;

    private String ecodedPassword;

    @BeforeEach()
    void setUp(){
        jwtToken = "Bearer test.jwt.token";
        refreshToken = "tets.refresh.token";

        ecodedPassword = "encode";

        userRoles = new HashSet<>();
        userRole = new Role();
        userRole.setName(UserRole.USER.getRole());
        userRoles.add(userRole);

        ownerRoles = new HashSet<>();
        ownerRole = new Role();
        ownerRole.setName(UserRole.OWNER.getRole());
        ownerRoles.add(ownerRole);

        user = new User();
        user.setId(1);
        user.setEmail("test@gmail.com");
        user.setPassword("pass123");
        user.setRoles(userRoles);

        owner = new User();
        owner.setId(2);
        owner.setEmail("test@gmail.com");
        owner.setPassword("pass123");
        owner.setRoles(ownerRoles);

        refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setId(1);
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setRefreshToken(refreshToken);

        registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail("test@gmail.com");
        registrationRequest.setPassword("pass123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@gmail.com");
        loginRequest.setPassword("pass123");

        userProfileDto = new UserProfileDto();
        userProfileDto.setId(1);
        userProfileDto.setRefreshToken(refreshToken);

        ownerProfileDto = new UserProfileDto();
        ownerProfileDto.setId(2);
        ownerProfileDto.setRefreshToken(refreshToken);
    }

    @Test
    void registerUser_Success(){
        when(roleRepository.findByName(UserRole.USER.getRole())).thenReturn(Optional.of(userRole));
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(false);
        when(userMapper.registrationRequestToUser(registrationRequest)).thenReturn(user);
        when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn(ecodedPassword);
        when(userRepository.save(user)).thenReturn(user);
        when(jwtTokenService.generateToken(user)).thenReturn(jwtToken);
        when(refreshTokenService.generateOrUpdateRefreshToken(user)).thenReturn(refreshTokenEntity);
        when(userMapper.userToUserProfileDto(user, jwtToken, refreshToken)).thenReturn(userProfileDto);

        AuthenticationResponse<UserProfileDto> result = authenticationService.registerUser(registrationRequest);

        assertNotNull(result);
        assertEquals(result.getPayload().getId(), user.getId());
        assertEquals(result.getPayload().getRefreshToken(), refreshToken);

        verify(roleRepository, times(1)).findByName(UserRole.USER.getRole());
        verify(userRepository, times(1)).existsByEmail(registrationRequest.getEmail());
        verify(userMapper, times(1)).registrationRequestToUser(registrationRequest);
        verify(passwordEncoder, times(1)).encode(registrationRequest.getPassword());
        verify(userRepository, times(1)).save(user);
        verify(jwtTokenService, times(1)).generateToken(user);
        verify(refreshTokenService, times(1)).generateOrUpdateRefreshToken(user);
        verify(userMapper, times(1)).userToUserProfileDto(user, jwtToken, refreshToken);
    }

    @Test
    void registerUser_EmailWasAlreadyExists_ThrowInvalidDataException(){
        when(roleRepository.findByName(UserRole.USER.getRole())).thenReturn(Optional.of(userRole));
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authenticationService.registerUser(registrationRequest))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("already exists");

        verify(roleRepository, times(1)).findByName(UserRole.USER.getRole());
        verify(userRepository, times(1)).existsByEmail(registrationRequest.getEmail());
        verify(userMapper, never()).registrationRequestToUser(registrationRequest);
        verify(passwordEncoder, never()).encode(registrationRequest.getPassword());
        verify(userRepository, never()).save(user);
        verify(jwtTokenService, never()).generateToken(user);
        verify(refreshTokenService, never()).generateOrUpdateRefreshToken(user);
        verify(userMapper, never()).userToUserProfileDto(user, jwtToken, refreshToken);
    }

    @Test
    void loginUser_Success(){
        Authentication authentication = mock(Authentication.class);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        when(authenticationManager.authenticate(authToken)).thenReturn(authentication);
        when(userRepository.findUserByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(jwtTokenService.generateToken(user)).thenReturn(jwtToken);
        when(refreshTokenService.generateOrUpdateRefreshToken(user)).thenReturn(refreshTokenEntity);
        when(userMapper.userToUserProfileDto(user, jwtToken, refreshToken)).thenReturn(userProfileDto);

        AuthenticationResponse<UserProfileDto> result = authenticationService.loginUser(loginRequest);

        assertNotNull(result);
        assertEquals(result.getPayload().getId(), user.getId());
        assertEquals(result.getPayload().getRefreshToken(), refreshToken);

        verify(authenticationManager, times(1)).authenticate(authToken);
        verify(userRepository, times(1)).findUserByEmail(loginRequest.getEmail());
        verify(jwtTokenService, times(1)).generateToken(user);
        verify(refreshTokenService, times(1)).generateOrUpdateRefreshToken(user);
        verify(userMapper, times(1)).userToUserProfileDto(user, jwtToken, refreshToken);
    }

    @Test
    void loginUser_IncorrectPasswordOrLogin_ThrowInvalidDataException(){
        LoginRequest wrongLoginRequest = new LoginRequest();
        wrongLoginRequest.setEmail("wrong_email@gmail.com");
        wrongLoginRequest.setPassword("wrong_password");

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                wrongLoginRequest.getEmail(),
                wrongLoginRequest.getPassword()
        );

        when(authenticationManager.authenticate(authToken)).thenThrow(new BadCredentialsException("Invalid"));

        assertThatThrownBy(() -> authenticationService.loginUser(wrongLoginRequest))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Invalid password or email");

        verify(authenticationManager, times(1)).authenticate(authToken);
        verify(userRepository, never()).findUserByEmail(loginRequest.getEmail());
        verify(jwtTokenService, never()).generateToken(user);
        verify(refreshTokenService, never()).generateOrUpdateRefreshToken(user);
        verify(userMapper, never()).userToUserProfileDto(user, jwtToken, refreshToken);
    }

    @Test
    void loginUser_UserNotFound_ThrowNotFoundException(){
        Authentication authentication = mock(Authentication.class);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        when(authenticationManager.authenticate(authToken)).thenReturn(authentication);
        when(userRepository.findUserByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.loginUser(loginRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("was not found");

        verify(authenticationManager, times(1)).authenticate(authToken);
        verify(userRepository, times(1)).findUserByEmail(loginRequest.getEmail());
        verify(jwtTokenService, never()).generateToken(user);
        verify(refreshTokenService, never()).generateOrUpdateRefreshToken(user);
        verify(userMapper, never()).userToUserProfileDto(user, jwtToken, refreshToken);
    }

    @Test
    void registerOwner_Success(){
        when(roleRepository.findByName(UserRole.OWNER.getRole())).thenReturn(Optional.of(ownerRole));
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(false);
        when(userMapper.registrationRequestToUser(registrationRequest)).thenReturn(owner);
        when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn(ecodedPassword);
        when(userRepository.save(owner)).thenReturn(owner);
        when(jwtTokenService.generateToken(owner)).thenReturn(jwtToken);
        when(refreshTokenService.generateOrUpdateRefreshToken(owner)).thenReturn(refreshTokenEntity);
        when(userMapper.userToUserProfileDto(owner, jwtToken, refreshToken)).thenReturn(ownerProfileDto);

        AuthenticationResponse<UserProfileDto> result = authenticationService.registerOwner(registrationRequest);

        assertNotNull(result);
        assertEquals(result.getPayload().getId(), owner.getId());
        assertEquals(result.getPayload().getRefreshToken(), refreshToken);

        verify(roleRepository, times(1)).findByName(UserRole.OWNER.getRole());
        verify(userRepository, times(1)).existsByEmail(registrationRequest.getEmail());
        verify(userMapper, times(1)).registrationRequestToUser(registrationRequest);
        verify(passwordEncoder, times(1)).encode(registrationRequest.getPassword());
        verify(userRepository, times(1)).save(owner);
        verify(jwtTokenService, times(1)).generateToken(owner);
        verify(refreshTokenService, times(1)).generateOrUpdateRefreshToken(owner);
        verify(userMapper, times(1)).userToUserProfileDto(owner, jwtToken, refreshToken);
    }

    @Test
    void registrationOwner_EmailWasAlreadyExists_ThrowInvalidDataException(){
        when(roleRepository.findByName(UserRole.OWNER.getRole())).thenReturn(Optional.of(ownerRole));
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authenticationService.registerOwner(registrationRequest))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("already exists");

        verify(roleRepository, times(1)).findByName(UserRole.OWNER.getRole());
        verify(userRepository, times(1)).existsByEmail(registrationRequest.getEmail());
        verify(userMapper, never()).registrationRequestToUser(registrationRequest);
        verify(passwordEncoder, never()).encode(registrationRequest.getPassword());
        verify(userRepository, never()).save(owner);
        verify(jwtTokenService, never()).generateToken(owner);
        verify(refreshTokenService, never()).generateOrUpdateRefreshToken(owner);
        verify(userMapper, never()).userToUserProfileDto(owner, jwtToken, refreshToken);
    }

    @Test
    void loginOwner_Success(){
        Authentication authentication = mock(Authentication.class);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        when(authenticationManager.authenticate(authToken)).thenReturn(authentication);
        when(userRepository.findUserByEmail(loginRequest.getEmail())).thenReturn(Optional.of(owner));
        when(jwtTokenService.generateToken(owner)).thenReturn(jwtToken);
        when(refreshTokenService.generateOrUpdateRefreshToken(owner)).thenReturn(refreshTokenEntity);
        when(userMapper.userToUserProfileDto(owner, jwtToken, refreshToken)).thenReturn(ownerProfileDto);

        AuthenticationResponse<UserProfileDto> result = authenticationService.loginOwner(loginRequest);

        assertNotNull(result);
        assertEquals(result.getPayload().getId(), owner.getId());
        assertEquals(result.getPayload().getRefreshToken(), refreshToken);

        verify(authenticationManager, times(1)).authenticate(authToken);
        verify(userRepository, times(1)).findUserByEmail(loginRequest.getEmail());
        verify(jwtTokenService, times(1)).generateToken(owner);
        verify(refreshTokenService, times(1)).generateOrUpdateRefreshToken(owner);
        verify(userMapper, times(1)).userToUserProfileDto(owner, jwtToken, refreshToken);
    }

    @Test
    void loginOwner_IncorrectPasswordOrLogin_ThrowInvalidDataException(){
        LoginRequest wrongLoginRequest = new LoginRequest();
        wrongLoginRequest.setEmail("wrong_email@gmail.com");
        wrongLoginRequest.setPassword("wrong_password");

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                wrongLoginRequest.getEmail(),
                wrongLoginRequest.getPassword()
        );

        when(authenticationManager.authenticate(authToken)).thenThrow(new BadCredentialsException("Invalid"));

        assertThatThrownBy(() -> authenticationService.loginOwner(wrongLoginRequest))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Invalid password or email");

        verify(authenticationManager, times(1)).authenticate(authToken);
        verify(userRepository, never()).findUserByEmail(loginRequest.getEmail());
        verify(jwtTokenService, never()).generateToken(owner);
        verify(refreshTokenService, never()).generateOrUpdateRefreshToken(owner);
        verify(userMapper, never()).userToUserProfileDto(owner, jwtToken, refreshToken);
    }

    @Test
    void loginOwner_OwnerNotFound_ThrowNotFoundException(){
        Authentication authentication = mock(Authentication.class);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        when(authenticationManager.authenticate(authToken)).thenReturn(authentication);
        when(userRepository.findUserByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.loginOwner(loginRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("was not found");

        verify(authenticationManager, times(1)).authenticate(authToken);
        verify(userRepository, times(1)).findUserByEmail(loginRequest.getEmail());
        verify(jwtTokenService, never()).generateToken(owner);
        verify(refreshTokenService, never()).generateOrUpdateRefreshToken(owner);
        verify(userMapper, never()).userToUserProfileDto(owner, jwtToken, refreshToken);
    }

    @Test
    void validateToken_Success_ShouldReturnValidResponse() {
        when(jwtTokenService.getEmail(jwtToken)).thenReturn("email");
        when(jwtTokenService.validateToken(jwtToken)).thenReturn(true);
        when(userRepository.findUserByEmail("email")).thenReturn(Optional.of(user));

        TokenValidationResponse response = authenticationService.validateToken(jwtToken);

        assertNotNull(response);
        assertTrue(response.isValid());
        assertEquals(user.getId(), response.getUserId());
        assertEquals(user.getEmail(), response.getEmail());

        verify(jwtTokenService, times(1)).getEmail(jwtToken);
        verify(jwtTokenService, times(1)).validateToken(jwtToken);
        verify(userRepository, times(1)).findUserByEmail("email");
        verify(jwtTokenService, times(1)).getRoles(jwtToken);
    }

    @Test
    void validateToken_EmptyEmail_() {
        when(jwtTokenService.getEmail(jwtToken)).thenReturn(null);

        TokenValidationResponse response = authenticationService.validateToken(jwtToken);

        assertNotNull(response);
        assertFalse(response.isValid());
        assertNull(response.getUserId());
        assertNull(response.getEmail());

        verify(jwtTokenService, times(1)).getEmail(jwtToken);
        verify(jwtTokenService, never()).validateToken(jwtToken);
        verify(userRepository, never()).findUserByEmail("email");
        verify(jwtTokenService, never()).getRoles(jwtToken);
    }


    @Test
    void refreshAccessToken_Success(){
        String newJwtToken = "Bearer new.jwt.token";

        when(refreshTokenService.validateAndRefreshRefreshToken(refreshToken)).thenReturn(refreshTokenEntity);
        when(jwtTokenService.generateToken(user)).thenReturn(newJwtToken);

        AuthenticationRefreshResponse result = authenticationService.refreshAccessToken(refreshToken);

        assertNotNull(result);
        assertNotNull(result.getToken());
        assertEquals(newJwtToken, result.getToken());

        verify(refreshTokenService, times(1)).validateAndRefreshRefreshToken(refreshToken);
        verify(jwtTokenService, times(1)).generateToken(user);
    }
}
