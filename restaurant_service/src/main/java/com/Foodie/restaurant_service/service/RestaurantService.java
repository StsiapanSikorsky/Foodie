package com.Foodie.restaurant_service.service;

import com.Foodie.restaurant_service.dto.RestaurantDto;
import com.Foodie.restaurant_service.request.restaurants.RestaurantRequest;
import com.Foodie.restaurant_service.request.restaurants.SearchRestaurantRequest;
import com.Foodie.restaurant_service.request.restaurants.UpdateRestaurantRequest;
import com.Foodie.restaurant_service.responce.PaginationResponse;
import com.Foodie.restaurant_service.responce.RestaurantResponse;
import com.Foodie.restaurant_service.responce.booking.RestaurantCheckResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;


public interface RestaurantService {
    RestaurantResponse<RestaurantDto> getRestaurantById(
            @NotNull Integer restaurantId);

    RestaurantResponse<RestaurantDto> addNewRestaurant(
            @NotNull @Valid RestaurantRequest request,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            @NotNull HttpServletResponse response);

    RestaurantResponse<RestaurantDto> updateRestaurant(
            @NotNull Integer restaurantId,
            @NotNull @Valid UpdateRestaurantRequest request,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            @NotNull HttpServletResponse response);

    void softDeleteRestaurant (
            @NotNull Integer restaurantId,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            @NotNull HttpServletResponse response);

    RestaurantResponse<PaginationResponse<RestaurantDto>> getAllRestaurants(
            @NotNull Pageable pageable);

    RestaurantResponse<PaginationResponse<RestaurantDto>> searchRestaurants(
            @NotNull SearchRestaurantRequest request,
            @NotNull Pageable pageable);

}
