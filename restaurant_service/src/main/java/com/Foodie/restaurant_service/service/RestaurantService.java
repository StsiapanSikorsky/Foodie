package com.Foodie.restaurant_service.service;

import com.Foodie.restaurant_service.dto.RestaurantDto;
import com.Foodie.restaurant_service.request.RestaurantRequest;
import com.Foodie.restaurant_service.request.restaurants.SearchRestaurantRequest;
import com.Foodie.restaurant_service.request.restaurants.UpdateRestaurantRequest;
import com.Foodie.restaurant_service.responce.PaginationResponce;
import com.Foodie.restaurant_service.responce.RestaurantResponce;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;


public interface RestaurantService {
    RestaurantResponce<RestaurantDto> getRestaurantById(@NotNull Integer restaurantId);

    RestaurantResponce<RestaurantDto> addNewRestaurant(@NotNull RestaurantRequest request, @NotNull String jwtToken);

    RestaurantResponce<RestaurantDto> updateRestaurant(@NotNull Integer restaurantId , @NotNull UpdateRestaurantRequest request, @NotNull String jwtToken);

    void softDeleteRestaurant (@NotNull Integer restaurantId, @NotNull String  jwtToken);

    RestaurantResponce<PaginationResponce<RestaurantDto>> getAllRestaurants(@NotNull Pageable pageable);

    RestaurantResponce<PaginationResponce<RestaurantDto>> searchRestaurants(@NotNull SearchRestaurantRequest request, @NotNull Pageable pageable);
}
