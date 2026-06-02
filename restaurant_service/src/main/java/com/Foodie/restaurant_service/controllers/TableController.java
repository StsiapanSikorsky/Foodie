package com.Foodie.restaurant_service.controllers;

import com.Foodie.restaurant_service.dto.RestaurantTableDto;
import com.Foodie.restaurant_service.request.tables.TableRequest;
import com.Foodie.restaurant_service.request.tables.UpdateTableRequest;
import com.Foodie.restaurant_service.responce.PaginationResponse;
import com.Foodie.restaurant_service.responce.RestaurantTableResponse;
import com.Foodie.restaurant_service.service.RestaurantTableService;
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
@RequestMapping("${end.point.restaurant.id.tables}")
public class TableController {

    private final RestaurantTableService restaurantTableService;
    private final Utils utils;

    @PostMapping
    public ResponseEntity<RestaurantTableResponse<RestaurantTableDto>> addRestaurantTable(
            @PathVariable(name = "id") Integer id,
            @RequestBody @Valid TableRequest request,
            @CookieValue (name = "Authorization", required = false) String jwtToken,
            @CookieValue (name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response

    ){
        String checkedJwt = utils.checkTokensInCookie(jwtToken, refreshToken, response);

        RestaurantTableResponse<RestaurantTableDto> result = restaurantTableService.addRestaurantTable(
                id,
                request,
                "Bearer " + checkedJwt,
                refreshToken,
                response
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(result);
    }

    @GetMapping
    public ResponseEntity<RestaurantTableResponse<PaginationResponse<RestaurantTableDto>>> getAllRestaurantTables(
            @PathVariable(name = "id") Integer id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "10") int limit
    ){
        Pageable pageable = PageRequest.of(page, limit);
        RestaurantTableResponse<PaginationResponse<RestaurantTableDto>> response = restaurantTableService.getAllTables(id, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("${end.point.tableId}")
    public ResponseEntity<RestaurantTableResponse<RestaurantTableDto>> getRestaurantTable(
            @PathVariable(name = "id") Integer id,
            @PathVariable(name = "numberOfTable") Integer numberOfTable
    ){
        RestaurantTableResponse<RestaurantTableDto> response = restaurantTableService.getTable(id, numberOfTable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping("${end.point.tableId}")
    public ResponseEntity<RestaurantTableResponse<RestaurantTableDto>> updateRestaurantTable(
            @PathVariable(name = "id") Integer id,
            @PathVariable(name = "numberOfTable") Integer numberOfTable,
            @RequestBody UpdateTableRequest request,
            @CookieValue (name = "Authorization", required = false) String jwtToken,
            @CookieValue (name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ){
        String checkedJwt = utils.checkTokensInCookie(jwtToken, refreshToken, response);

        RestaurantTableResponse<RestaurantTableDto> result = restaurantTableService.updateTable(
                id,
                numberOfTable,
                request,
                "Bearer " + checkedJwt,
                refreshToken,
                response
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    @DeleteMapping("${end.point.tableId}")
    public ResponseEntity<Void> deleteRestaurantTable(
            @PathVariable(name = "id") Integer id,
            @PathVariable(name = "numberOfTable") Integer numberOfTable,
            @CookieValue (name = "Authorization", required = false) String jwtToken,
            @CookieValue (name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ){
        String checkedJwt = utils.checkTokensInCookie(jwtToken, refreshToken, response);

        restaurantTableService.deleteTable(
                id,
                numberOfTable,
                "Bearer " + checkedJwt,
                refreshToken,
                response
        );

        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }
}
