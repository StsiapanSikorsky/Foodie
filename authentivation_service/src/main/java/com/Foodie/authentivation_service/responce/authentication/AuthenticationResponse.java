package com.Foodie.authentivation_service.responce.authentication;

import com.Foodie.authentivation_service.enums.LogMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse<P> implements Serializable {
    private String message;
    private P payload;
    private boolean success;

    public static <P extends Serializable> AuthenticationResponse<P> createSuccessful (P payload){
        return new AuthenticationResponse<>(StringUtils.EMPTY, payload, true);
    }

    public static <P extends Serializable> AuthenticationResponse<P> createSuccessfulWithNewToken (P payload){
        return new AuthenticationResponse<>(LogMessage.TOKEN_CREATED_OR_UPDATED.getMessage(), payload, true);
    }
}
