package com.Foodie.authentivation_service.advice;

import com.Foodie.authentivation_service.enums.ErrorMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AccessRegistrationHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException
    {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write(ErrorMessage.HAVE_NO_ACCECSS.getMessage());
    }
}
