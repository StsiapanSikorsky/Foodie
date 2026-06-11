package com.Foodie.restaurant_service.controllers;

import com.Foodie.restaurant_service.responce.booking.RestaurantCheckResponse;
import com.Foodie.restaurant_service.service.BookingIntegrationService;
import com.Foodie.restaurant_service.enums.LogMessage;
import com.Foodie.restaurant_service.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("${end.point.booking-integration}")
@Tag(name = "Внутрисервисные контроллеры для Booking Service")
public class BookingIntegrationController {

    private final BookingIntegrationService bookingIntegrationService;

    @GetMapping("${end.point.check.restaurantId.userId.numberOfTable}")
    @Operation(
            summary = "Проверка прав пользователя и получения данных о ресторане"
    )
    public ResponseEntity<RestaurantCheckResponse> existRestaurantByIdAndCheckOwner(
            @PathVariable(name = "restaurantId") Integer restaurantId,
            @PathVariable (name = "userId") Integer userId,
            @PathVariable (name = "numberOfTable") Integer numberOfTable
    ){
        log.info(LogMessage.METHOD_API_CALLED_WITH_3_PARAM.getMessage(Utils.getMethodName(), "restaurantId:" + restaurantId, "userId:" + userId, "numberOfTable:" + numberOfTable));

        RestaurantCheckResponse result = bookingIntegrationService.existRestaurantByIdAndCheckOwner(restaurantId, userId, numberOfTable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("${end.point.getRestaurantId.ownerId}")
    @Operation(
            summary = "Получение Id ресторана если запрос выполнял владелец"
    )
    public ResponseEntity<Integer> getRestaurantIdWhenUserIsOwner(
            @PathVariable (name = "ownerId") Integer ownerId
    ){
        log.info(LogMessage.METHOD_API_CALLED_WITH_1_PARAM.getMessage(Utils.getMethodName(), "ownerId:" + ownerId));

        Integer result = bookingIntegrationService.getRestaurantIdWhenUserIsOwner(ownerId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("${end.point.check}")
    @Operation(
            summary = "Проверка является ли пользователь владельцем"
    )
    public ResponseEntity<Boolean> isOwner(
            @RequestParam(name = "restaurantId") Integer restaurantId,
            @RequestParam (name = "userId") Integer userId
    ){
        log.info(LogMessage.METHOD_API_CALLED_WITH_2_PARAM.getMessage(Utils.getMethodName(), "restaurantId:" + restaurantId, "userId:" + userId));

        Boolean result = bookingIntegrationService.checkRestaurantOwner(restaurantId, userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }
}
