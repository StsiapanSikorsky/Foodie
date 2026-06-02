package com.Foodie.restaurant_service.service;

import com.Foodie.restaurant_service.responce.booking.RestaurantCheckResponse;
import jakarta.validation.constraints.NotNull;

public interface BookingIntegrationService {

    RestaurantCheckResponse existRestaurantByIdAndCheckOwner(
            @NotNull Integer restaurantId,
            @NotNull Integer userId,
            @NotNull Integer numberOfTable);

    Integer getRestaurantIdWhenUserIsOwner(
            @NotNull Integer ownerId);

    Boolean checkRestaurantOwner(
            @NotNull Integer restaurantId,
            @NotNull Integer userId
    );
}
