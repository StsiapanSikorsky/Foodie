package com.Foodie.restaurant_service.responce;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponce<T> implements Serializable {

    private String message;
    private T payload;
    private boolean success;

    public static <T> RestaurantResponce<T> createSuccessful(T payload) {
        RestaurantResponce<T> response = new RestaurantResponce<>();
        response.setSuccess(true);
        response.setPayload(payload);
        response.setMessage(StringUtils.EMPTY);
        return response;
    }

    public static <T> RestaurantResponce<T> createError(String message) {
        RestaurantResponce<T> response = new RestaurantResponce<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setPayload(null);
        return response;
    }
}