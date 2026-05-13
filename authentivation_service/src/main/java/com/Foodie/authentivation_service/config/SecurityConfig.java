package com.Foodie.authentivation_service.config;

import com.Foodie.authentivation_service.advice.AccessRegistrationHandler;
import com.Foodie.authentivation_service.enums.UserRole;
import com.Foodie.authentivation_service.security.filter.JwtRequestFilter;
import com.Foodie.authentivation_service.services.AuthenticationService;
import com.Foodie.authentivation_service.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AccessRegistrationHandler accessRegistrationHandler;
    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception
    {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(
            UserService userService
    ){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception
    {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/authentication/user/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/authentication/user/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/authentication/owner/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/authentication/owner/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/authentication/validate").permitAll()

                        .requestMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "//webjars/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/user/{id}").hasAnyAuthority(userAccessSecurityRole())
                        .requestMatchers(HttpMethod.PUT, "/user/{id}").hasAnyAuthority(userAccessSecurityRole())
                        .requestMatchers(HttpMethod.DELETE, "/user/{id}").hasAnyAuthority(userAccessSecurityRole())

                        .requestMatchers(HttpMethod.GET, "/owner/{id}").hasAnyAuthority(ownerAccessSecurityRole())
                        .requestMatchers(HttpMethod.PUT, "/owner/{id}").hasAnyAuthority(ownerAccessSecurityRole())
                        .requestMatchers(HttpMethod.DELETE, "/owner/{id}").hasAnyAuthority(ownerAccessSecurityRole())

                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler(accessRegistrationHandler)
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private String userAccessSecurityRole(){
        return UserRole.USER.name();
    }

    private String ownerAccessSecurityRole(){
        return UserRole.OWNER.name();
    }

}
