package com.Foodie.restaurant_service.controllers;

import com.Foodie.restaurant_service.dto.RestaurantDto;
import com.Foodie.restaurant_service.enums.MethodsHTTP;
import com.Foodie.restaurant_service.request.restaurants.RestaurantRequest;
import com.Foodie.restaurant_service.request.restaurants.SearchRestaurantRequest;
import com.Foodie.restaurant_service.request.restaurants.UpdateRestaurantRequest;
import com.Foodie.restaurant_service.responce.PaginationResponse;
import com.Foodie.restaurant_service.responce.RestaurantResponse;
import com.Foodie.restaurant_service.service.RestaurantService;
import com.Foodie.restaurant_service.enums.LogMessage;
import com.Foodie.restaurant_service.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("${end.point.restaurant}")
@Tag(name = "Restaurant controller")
public class RestaurantsController {

    private final RestaurantService restaurantService;
    private final Utils utils;

    @GetMapping("${end.point.id}")
    @Operation(
            summary = "Получение ресторана по Id"
    )
    public ResponseEntity<RestaurantResponse<RestaurantDto>> getRestaurantById(
            @PathVariable(name = "id") Integer id
    ){
        log.info(LogMessage.METHOD_API_CALLED.getMessage(MethodsHTTP.GET ,Utils.getMethodName()));

        RestaurantResponse<RestaurantDto> result = restaurantService.getRestaurantById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    @PostMapping("${end.point.add}")
    @Operation(
            summary = "Добавление нового ресторана"
    )
    public ResponseEntity<RestaurantResponse<RestaurantDto>> addRestaurant(
            @RequestBody @Valid RestaurantRequest request,
            @CookieValue(name = "Authorization", required = false) String jwtToken,
            @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ){
        log.info(LogMessage.METHOD_API_CALLED.getMessage(MethodsHTTP.POST, Utils.getMethodName()));

        String checkedJwt = utils.checkTokensInCookie(jwtToken, refreshToken, response);

        RestaurantResponse<RestaurantDto> result =
                restaurantService.addNewRestaurant(
                        request,
                        "Bearer " + checkedJwt,
                        refreshToken,
                        response
                );

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("${end.point.id}")
    @Operation(
            summary = "Обновление данных ресторана"
    )
    public ResponseEntity<RestaurantResponse<RestaurantDto>> updateRestaurant(
            @PathVariable (name = "id") Integer id,
            @RequestBody @Valid UpdateRestaurantRequest request,
            @CookieValue (name = "Authorization", required = false) String jwtToken,
            @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ){
        log.info(LogMessage.METHOD_API_CALLED.getMessage(MethodsHTTP.PUT, Utils.getMethodName()));

        String checkedJwt = utils.checkTokensInCookie(jwtToken, refreshToken, response);

        RestaurantResponse<RestaurantDto> result = restaurantService.updateRestaurant(
                id,
                request,
                "Bearer " + checkedJwt,
                refreshToken,
                response
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    @DeleteMapping("${end.point.id}")
    @Operation(
            summary = "Мягкое удаление ресторана"
    )
    public ResponseEntity<Void> softDeleteRestaurant(
            @PathVariable(name = "id") Integer  id,
            @CookieValue(name = "Authorization", required = false) String jwtToken,
            @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ){
        log.info(LogMessage.METHOD_API_CALLED.getMessage(MethodsHTTP.DELETE, Utils.getMethodName()));

        String checkedJwt = utils.checkTokensInCookie(jwtToken, refreshToken, response);

        restaurantService.softDeleteRestaurant(
                id,
                "Bearer " + checkedJwt,
                refreshToken,
                response
        );

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping
    @Operation(
            summary = "Получение списка всех ресторанов"
    )
    public ResponseEntity<RestaurantResponse<PaginationResponse<RestaurantDto>>> getAllRestaurants(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "10") int limit
    ){
        log.info(LogMessage.METHOD_API_CALLED.getMessage(MethodsHTTP.GET, Utils.getMethodName()));

        Pageable pageable = PageRequest.of(page, limit);
        RestaurantResponse<PaginationResponse<RestaurantDto>> result = restaurantService.getAllRestaurants(pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("${end.point.search}")
    @Operation(
            summary = "Поиск ресторана по параметрам: названию, городу, типу, ключевым словам"
    )
    public ResponseEntity<RestaurantResponse<PaginationResponse<RestaurantDto>>> searchRestaurants(
        @RequestBody @Valid SearchRestaurantRequest request,
        @RequestParam (name = "page", defaultValue = "0") int page,
        @RequestParam (name = "limit", defaultValue = "10") int limit
    ){
        log.info(LogMessage.METHOD_API_CALLED.getMessage(MethodsHTTP.GET, Utils.getMethodName()));

        Pageable pageable = PageRequest.of(page, limit);
        RestaurantResponse<PaginationResponse<RestaurantDto>> result = restaurantService.searchRestaurants(request, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }
}
