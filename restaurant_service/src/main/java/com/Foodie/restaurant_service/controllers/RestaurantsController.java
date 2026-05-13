package com.Foodie.restaurant_service.controllers;

import com.Foodie.restaurant_service.advice.exceptions.NotFoundException;
import com.Foodie.restaurant_service.advice.exceptions.UnauthorizedException;
import com.Foodie.restaurant_service.dto.RestaurantDto;
import com.Foodie.restaurant_service.request.RestaurantRequest;
import com.Foodie.restaurant_service.request.restaurants.SearchRestaurantRequest;
import com.Foodie.restaurant_service.request.restaurants.UpdateRestaurantRequest;
import com.Foodie.restaurant_service.responce.PaginationResponce;
import com.Foodie.restaurant_service.responce.RestaurantResponce;
import com.Foodie.restaurant_service.service.RestaurantService;
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

    @GetMapping("${end.point.id}")
    public ResponseEntity<RestaurantResponce<RestaurantDto>> getRestaurantById(
            @PathVariable(name = "id") Integer id
    ){
        RestaurantResponce<RestaurantDto> response = restaurantService.getRestaurantById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("${end.point.add}")
    public ResponseEntity<RestaurantResponce<RestaurantDto>> addRestaurant(
            @RequestBody @Valid RestaurantRequest request,
            @CookieValue(name = "Authorization", required = false) String jwtToken
    ){
        if(jwtToken == null) {
            throw new NotFoundException("Token not found in cookie");
        }

        RestaurantResponce<RestaurantDto> response =
                restaurantService.addNewRestaurant(
                        request,
                        "Bearer " + jwtToken
                );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("${end.point.id}")
    public ResponseEntity<RestaurantResponce<RestaurantDto>> updateRestaurant(
            @PathVariable (name = "id") Integer id,
            @RequestBody @Valid UpdateRestaurantRequest request,
            @CookieValue (name = "Authorization", required = false) String jwtToken
    ){
        if(jwtToken == null) {
            throw new NotFoundException("Token not found in cookie");
        }

        RestaurantResponce<RestaurantDto> response = restaurantService.updateRestaurant(
                id,
                request,
                "Bearer " + jwtToken
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("${end.point.id}")
    public ResponseEntity<Void> softDeleteRestaurant(
            @PathVariable(name = "id") Integer  id,
            @CookieValue(name = "Authorization", required = false) String jwtToken
    ){
        if(jwtToken == null) {
            throw new UnauthorizedException("Token not found in cookie");
        }

        restaurantService.softDeleteRestaurant(
                id,
                "Bearer " + jwtToken
        );

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping
    public ResponseEntity<RestaurantResponce<PaginationResponce<RestaurantDto>>> getAllRestaurants(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "10") int limit
    ){
        Pageable pageable = PageRequest.of(page, limit);
        RestaurantResponce<PaginationResponce<RestaurantDto>> response = restaurantService.getAllRestaurants(pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("${end.point.search}")
    public ResponseEntity<RestaurantResponce<PaginationResponce<RestaurantDto>>> searchRestaurants(
        @RequestBody @Valid SearchRestaurantRequest request,
        @RequestParam (name = "page", defaultValue = "0") int page,
        @RequestParam (name = "limit", defaultValue = "10") int limit
    ){
        Pageable pageable = PageRequest.of(page, limit);

        RestaurantResponce<PaginationResponce<RestaurantDto>> response = restaurantService.searchRestaurants(request, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }


    //TODO: Добавить логирование и Unit тесты
}
