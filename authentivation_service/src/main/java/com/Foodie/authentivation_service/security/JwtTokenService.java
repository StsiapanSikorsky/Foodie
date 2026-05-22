package com.Foodie.authentivation_service.security;

import com.Foodie.authentivation_service.entity.Role;
import com.Foodie.authentivation_service.entity.User;
import com.Foodie.authentivation_service.enums.AuthenticationConstants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.io.Decoders;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Component
public class JwtTokenService {

    private final SecretKey secretKey;
    private final Long jwtValidityinMilliseconds;

    public JwtTokenService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.lifetime}") long jwtValidityinMilliseconds
    ){
        this.secretKey = getKey(secret);
        this.jwtValidityinMilliseconds = jwtValidityinMilliseconds;
    }

    private SecretKey getKey(
            String secretKey64
    ){
        byte[] decode64 = Decoders.BASE64.decode(secretKey64);
        return Keys.hmacShaKeyFor(decode64);
    }

    public String generateToken(
            @NonNull User user
    ){
        Map<String,Object> claims = new HashMap<>();

        claims.put(AuthenticationConstants.USER_ID, user.getId());
        claims.put(AuthenticationConstants.USER_NAME, user.getUserName());
        claims.put(AuthenticationConstants.EMAIL, user.getEmail());

        List<String> roleList = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        claims.put(AuthenticationConstants.ROLES, roleList);

        return createToken(claims, user.getEmail());
    }

    private String createToken(
            Map<String, Object> claims,
            String subject
    ){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtValidityinMilliseconds))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String refreshToken(
            String token
    ){
        Claims claims = getAllClaimsFromToken(token);
        return createToken(claims, claims.getSubject());
    }

    public boolean validateToken(
            String token
    ){
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        }
        catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

    public String getUsername(
            String token
    ){
        Claims claims = getAllClaimsFromToken(token);
        return claims.get(AuthenticationConstants.USER_NAME, String.class);
    }

    public String getUserId(
            String token
    ){
        Claims claims = getAllClaimsFromToken(token);
        return String.valueOf(claims.get(AuthenticationConstants.USER_ID));
    }

    public String getEmail(
            String token
    ){
        Claims claims = getAllClaimsFromToken(token);
        return String.valueOf(claims.get(AuthenticationConstants.EMAIL));
    }

    public List<String> getRoles(
            String token
    ){
        Claims claims = getAllClaimsFromToken(token);
        return claims.get(AuthenticationConstants.ROLES, List.class);
    }

    private Claims getAllClaimsFromToken(
            String token
    ){
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
        catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }

}
