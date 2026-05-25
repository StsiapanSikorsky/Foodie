package com.Foodie.restaurant_service.responce;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantTableResponse<T> implements Serializable {
    private String message;
    private T payload;
    private boolean success;

    public static <T> RestaurantTableResponse<T> createSuccessful(T payload) {
        RestaurantTableResponse<T> response = new RestaurantTableResponse<>();
        response.setSuccess(true);
        response.setPayload(payload);
        response.setMessage(StringUtils.EMPTY);
        return response;
    }
}
