package com.Foodie.authentivation_service.responce.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse<P> implements Serializable {
    private String message;
    private P payload;
    private boolean success;

    public static <P extends Serializable> UserResponse<P> createSuccessful (P payload){
        return new UserResponse<>(StringUtils.EMPTY, payload, true);
    }

//    public static <P extends Serializable> UserResponse<P> createSuccessfulWithNewToken (P payload){
//        return new UserResponse<>(LogMessage.TOKEN_CREATED_OR_UPDATED.getMessage(), payload, true);
//    }
}
