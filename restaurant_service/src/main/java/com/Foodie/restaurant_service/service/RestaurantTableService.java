package com.Foodie.restaurant_service.service;


import com.Foodie.restaurant_service.dto.RestaurantTableDto;
import com.Foodie.restaurant_service.request.tables.TableRequest;
import com.Foodie.restaurant_service.request.tables.UpdateTableRequest;
import com.Foodie.restaurant_service.responce.PaginationResponse;
import com.Foodie.restaurant_service.responce.RestaurantTableResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;

public interface RestaurantTableService {

    //TODO: Заменить RestaurantResponse на TableResponse либо смена названия класса

    RestaurantTableResponse<RestaurantTableDto> addRestaurantTable(
            @NotNull Integer restaurantId,
            @NotNull TableRequest request,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            HttpServletResponse response
    );

    RestaurantTableResponse<PaginationResponse<RestaurantTableDto>> getAllTables(
            @NotNull Integer restaurantId,
            @NotNull Pageable pageable
    );

    RestaurantTableResponse<RestaurantTableDto> getTable(
            @NotNull Integer restaurantId,
            @NotNull Integer numberOfTable
    );

    RestaurantTableResponse<RestaurantTableDto> updateTable(
            @NotNull Integer restaurantId,
            @NotNull Integer numberOfTable,
            @NotNull UpdateTableRequest request,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            HttpServletResponse response
    );

    void deleteTable(
            @NotNull Integer restaurantId,
            @NotNull Integer numberOfTable,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            HttpServletResponse response
    );

}
