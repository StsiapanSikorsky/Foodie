package com.Foodie.authentivation_service.security.filter;

import com.Foodie.authentivation_service.enums.ErrorMessage;
import com.Foodie.authentivation_service.security.JwtTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import io.jsonwebtoken.SignatureException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USER_LOGIN_PATH = "/authentication/user/login";
    private static final String USER_REGISTER_PATH = "/authentication/user/register";

    private final JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        Optional<String> authHeader = Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER));
        String requestURI = request.getRequestURI();

        if (authHeader.isPresent() && authHeader.get().startsWith(BEARER_PREFIX)) {
            String jwt = authHeader.get().substring(BEARER_PREFIX.length());
            try {
                if (!jwtTokenService.validateToken(jwt)) {
                    throw new ExpiredJwtException(null, null, ErrorMessage.TOKEN_EXPIRED.getMessage());
                }

                Optional<String> emailOpt = Optional.ofNullable(jwtTokenService.getUsername(jwt));
                Optional<String> userIdOpt = Optional.ofNullable(jwtTokenService.getUserId(jwt));

                if(emailOpt.isPresent() && userIdOpt.isPresent()){
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        List<SimpleGrantedAuthority> authorities = jwtTokenService.getRoles(jwt).stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());

                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                emailOpt.get(),
                                jwt,
                                authorities
                        );
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            } catch (ExpiredJwtException e) {
                handleTokenExpiration(requestURI, jwt, response);
                return;
            } catch (SignatureException | MalformedJwtException e) {
                handleSignatureException(response);
                return;
            } catch (Exception e) {
                handleUnexpectedException(response, e);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.contains("/authentication/refresh")
                || uri.contains("/authentication/validate")
                || uri.contains("/authentication/user/login")
                || uri.contains("/authentication/user/register")
                || uri.contains("/authentication/owner/login")
                || uri.contains("/authentication/owner/register")
                || uri.contains("/swagger-ui")
                || uri.contains("/v3/api-docs");
    }

    private void handleTokenExpiration(
            String requestURI,
            String jwt,
            HttpServletResponse response
    ) throws IOException
    {
        if (isAuthEndpoint(requestURI)) {
            String refreshedToken = jwtTokenService.refreshToken(jwt);
            response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + refreshedToken);
        } else {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, ErrorMessage.TOKEN_EXPIRED.getMessage());
        }
    }

    private void handleSignatureException(
            HttpServletResponse response
    ) throws IOException
    {
        sendErrorResponse(response, HttpStatus.UNAUTHORIZED, ErrorMessage.INVALID_TOKEN_SIGNATURE.getMessage());
    }

    private void handleUnexpectedException(
            HttpServletResponse response,
            Exception e
    ) throws IOException {
        log.error(ErrorMessage.ERROR_DURING_JWT_PROCESSING.getMessage(), e);
        sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.UNEXPECTED_ERROR_OCCURRED.getMessage());
    }

    private void sendErrorResponse(
            HttpServletResponse response,
            HttpStatus status,
            String message
    ) throws IOException {
        response.setStatus(status.value());
        response.getWriter().write(message);
    }

    private boolean isAuthEndpoint(String uri) {
        return uri.equals(USER_LOGIN_PATH) || uri.equals(USER_REGISTER_PATH);
    }
}
