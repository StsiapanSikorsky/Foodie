package com.Foodie.restaurant_service.controllers;

import com.Foodie.restaurant_service.responce.booking.RestaurantCheckResponse;
import com.Foodie.restaurant_service.service.BookingIntegrationService;
import com.Foodie.restaurant_service.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/booking-integration")
public class BookingIntegrationController {

    private final BookingIntegrationService bookingIntegrationService;

    @GetMapping("/check/{restaurantId}/{userId}/{numberOfTable}")
    public ResponseEntity<RestaurantCheckResponse> existRestaurantByIdAndCheckOwner(
            @PathVariable(name = "restaurantId") Integer restaurantId,
            @PathVariable (name = "userId") Integer userId,
            @PathVariable (name = "numberOfTable") Integer numberOfTable
    ){
        RestaurantCheckResponse result = bookingIntegrationService.existRestaurantByIdAndCheckOwner(restaurantId, userId, numberOfTable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("/getRestaurantId/{ownerId}")
    public ResponseEntity<Integer> getRestaurantIdWhenUserIsOwner(
            @PathVariable (name = "ownerId") Integer ownerId
    ){
        Integer result = bookingIntegrationService.getRestaurantIdWhenUserIsOwner(ownerId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> isOwner(
            @RequestParam(name = "restaurantId") Integer restaurantId,
            @RequestParam (name = "userId") Integer userId
    ){
        Boolean result = bookingIntegrationService.checkRestaurantOwner(restaurantId, userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }
}
