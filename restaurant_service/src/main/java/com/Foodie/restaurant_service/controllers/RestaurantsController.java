package com.Foodie.restaurant_service.controllers;

import com.Foodie.restaurant_service.dto.RestaurantDto;
import com.Foodie.restaurant_service.request.restaurants.RestaurantRequest;
import com.Foodie.restaurant_service.request.restaurants.SearchRestaurantRequest;
import com.Foodie.restaurant_service.request.restaurants.UpdateRestaurantRequest;
import com.Foodie.restaurant_service.responce.PaginationResponse;
import com.Foodie.restaurant_service.responce.RestaurantResponse;
import com.Foodie.restaurant_service.responce.booking.RestaurantCheckResponse;
import com.Foodie.restaurant_service.service.RestaurantService;
import com.Foodie.restaurant_service.utils.Utils;
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
public class RestaurantsController {

    private final RestaurantService restaurantService;
    private final Utils utils;

    @GetMapping("${end.point.id}")
    public ResponseEntity<RestaurantResponse<RestaurantDto>> getRestaurantById(
            @PathVariable(name = "id") Integer id
    ){
        RestaurantResponse<RestaurantDto> result = restaurantService.getRestaurantById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    @PostMapping("${end.point.add}")
    public ResponseEntity<RestaurantResponse<RestaurantDto>> addRestaurant(
            @RequestBody @Valid RestaurantRequest request,
            @CookieValue(name = "Authorization", required = false) String jwtToken,
            @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ){
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
    public ResponseEntity<RestaurantResponse<RestaurantDto>> updateRestaurant(
            @PathVariable (name = "id") Integer id,
            @RequestBody @Valid UpdateRestaurantRequest request,
            @CookieValue (name = "Authorization", required = false) String jwtToken,
            @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ){
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
    public ResponseEntity<Void> softDeleteRestaurant(
            @PathVariable(name = "id") Integer  id,
            @CookieValue(name = "Authorization", required = false) String jwtToken,
            @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ){
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
    public ResponseEntity<RestaurantResponse<PaginationResponse<RestaurantDto>>> getAllRestaurants(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "10") int limit
    ){
        Pageable pageable = PageRequest.of(page, limit);
        RestaurantResponse<PaginationResponse<RestaurantDto>> result = restaurantService.getAllRestaurants(pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("${end.point.search}")
    public ResponseEntity<RestaurantResponse<PaginationResponse<RestaurantDto>>> searchRestaurants(
        @RequestBody @Valid SearchRestaurantRequest request,
        @RequestParam (name = "page", defaultValue = "0") int page,
        @RequestParam (name = "limit", defaultValue = "10") int limit
    ){
        Pageable pageable = PageRequest.of(page, limit);

        RestaurantResponse<PaginationResponse<RestaurantDto>> result = restaurantService.searchRestaurants(request, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }
}
