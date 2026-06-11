package com.Foodie.authentivation_service.utils;

import com.Foodie.authentivation_service.advice.exception.NotFoundException;
import com.Foodie.authentivation_service.entity.User;
import com.Foodie.authentivation_service.enums.ErrorMessage;
import com.Foodie.authentivation_service.enums.UserRole;
import com.Foodie.authentivation_service.repository.UserRepository;
import com.Foodie.authentivation_service.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AccessValidator {

    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    public void  validateAdminOrOwnerAccess(
            Integer ownerId
    ){
        Integer currentUserId = getUserIdFromAuthentication();

        if(!currentUserId.equals(ownerId)
                && !isAdminOrSuperAdmin(currentUserId))
        {
            log.warn(ErrorMessage.HAVE_NO_ACCESS.getMessage());
            throw new AccessDeniedException(ErrorMessage.HAVE_NO_ACCESS.getMessage());
        }
    }

    public Integer getUserIdFromAuthentication(){
        String jwtToken = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        return Integer.parseInt(jwtTokenService.getUserId(jwtToken));
    }

    public boolean isAdminOrSuperAdmin(
            Integer userId
    ){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn(ErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId));
                    return new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId));
                });

        return user.getRoles().stream()
                .map(role -> UserRole.fromName(role.getName()))
                .anyMatch(role -> role == UserRole.ADMIN);
    }
}
