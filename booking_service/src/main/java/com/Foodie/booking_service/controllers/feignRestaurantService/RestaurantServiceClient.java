package com.Foodie.booking_service.controllers.feignRestaurantService;

import com.Foodie.booking_service.response.restaurant.RestaurantCheckResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "restaurantService",
            url = "${restaurant.service.url:http//localhost:8198}")
public interface RestaurantServiceClient {

    @GetMapping("/restaurant/check/{restaurantId}/{userId}")
    RestaurantCheckResponse getRestaurantIdAndCheckOwner(
        @PathVariable ("restaurantId") Integer restaurantId,
        @PathVariable ("userId") Integer userId,
        @PathVariable ("numberOfTable") Integer numberOfTable
    );
}
