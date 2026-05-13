package com.Foodie.authentivation_service.responce.owner;

import com.Foodie.authentivation_service.enums.LogMessage;
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
public class OwnerResponse<P> implements Serializable {
    private String message;
    private P payload;
    private boolean success;

    public static <P extends Serializable> OwnerResponse<P> createSuccessful (P payload){
        return new OwnerResponse<>(StringUtils.EMPTY, payload, true);
    }

    public static <P extends Serializable> OwnerResponse<P> createSuccessfulWithNewToken (P payload){
        return new OwnerResponse<>(LogMessage.TOKEN_CREATED_OR_UPDATED.getMessage(), payload, true);
    }
}
