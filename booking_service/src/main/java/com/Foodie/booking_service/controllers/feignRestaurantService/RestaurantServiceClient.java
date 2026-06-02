package com.Foodie.booking_service.controllers.feignRestaurantService;

import com.Foodie.booking_service.response.restaurant.RestaurantCheckResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "restaurantService",
            url = "${restaurant.service.url:http://localhost:8198}")
public interface RestaurantServiceClient {

    @GetMapping("/booking-integration/check/{restaurantId}/{userId}/{numberOfTable}")
    RestaurantCheckResponse getRestaurantIdAndCheckOwner(
        @PathVariable ("restaurantId") Integer restaurantId,
        @PathVariable ("userId") Integer userId,
        @PathVariable ("numberOfTable") Integer numberOfTable
    );

    @GetMapping("/booking-integration/getRestaurantId/{ownerId}")
    Integer getRestaurantIdWhenUserIsOwner(
            @PathVariable ("ownerId") Integer userId
    );

    @GetMapping("/booking-integration/check")
    Boolean checkRestaurantOwner(
            @RequestParam (name = "restaurantId") Integer restaurantId,
            @RequestParam (name = "userId") Integer userId
    );
}
