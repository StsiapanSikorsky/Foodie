package com.Foodie.restaurant_service.controllers;

import com.Foodie.restaurant_service.dto.RestaurantTableDto;
import com.Foodie.restaurant_service.enums.MethodsHTTP;
import com.Foodie.restaurant_service.request.tables.TableRequest;
import com.Foodie.restaurant_service.request.tables.UpdateTableRequest;
import com.Foodie.restaurant_service.responce.PaginationResponse;
import com.Foodie.restaurant_service.responce.RestaurantTableResponse;
import com.Foodie.restaurant_service.service.RestaurantTableService;
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
@RequestMapping("${end.point.restaurant.id.tables}")
@Tag(name = "Table controller")
public class TableController {

    private final RestaurantTableService restaurantTableService;
    private final Utils utils;

    @PostMapping
    @Operation(
            summary = "Добавление стола в ресторан"
    )
    public ResponseEntity<RestaurantTableResponse<RestaurantTableDto>> addRestaurantTable(
            @PathVariable(name = "id") Integer id,
            @RequestBody @Valid TableRequest request,
            @CookieValue (name = "Authorization", required = false) String jwtToken,
            @CookieValue (name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response

    ){
        log.info(LogMessage.METHOD_API_CALLED.getMessage(MethodsHTTP.POST, Utils.getMethodName()));

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
    @Operation(
            summary = "Получение списка столов в ресторане"
    )
    public ResponseEntity<RestaurantTableResponse<PaginationResponse<RestaurantTableDto>>> getAllRestaurantTables(
            @PathVariable(name = "id") Integer id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "10") int limit
    ){
        log.info(LogMessage.METHOD_API_CALLED.getMessage(MethodsHTTP.GET, Utils.getMethodName()));

        Pageable pageable = PageRequest.of(page, limit);
        RestaurantTableResponse<PaginationResponse<RestaurantTableDto>> response = restaurantTableService.getAllTables(id, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("${end.point.tableId}")
    @Operation(
            summary = "Получение стола по Id"
    )
    public ResponseEntity<RestaurantTableResponse<RestaurantTableDto>> getRestaurantTable(
            @PathVariable(name = "id") Integer id,
            @PathVariable(name = "numberOfTable") Integer numberOfTable
    ){
        log.info(LogMessage.METHOD_API_CALLED.getMessage(MethodsHTTP.GET, Utils.getMethodName()));

        RestaurantTableResponse<RestaurantTableDto> response = restaurantTableService.getTable(id, numberOfTable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping("${end.point.tableId}")
    @Operation(
            summary = "Обновление данных о столе"
    )
    public ResponseEntity<RestaurantTableResponse<RestaurantTableDto>> updateRestaurantTable(
            @PathVariable(name = "id") Integer id,
            @PathVariable(name = "numberOfTable") Integer numberOfTable,
            @RequestBody UpdateTableRequest request,
            @CookieValue (name = "Authorization", required = false) String jwtToken,
            @CookieValue (name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ){
        log.info(LogMessage.METHOD_API_CALLED.getMessage(MethodsHTTP.PUT, Utils.getMethodName()));

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
    @Operation(
            summary = "Удаление стола у ресторана"
    )
    public ResponseEntity<Void> deleteRestaurantTable(
            @PathVariable(name = "id") Integer id,
            @PathVariable(name = "numberOfTable") Integer numberOfTable,
            @CookieValue (name = "Authorization", required = false) String jwtToken,
            @CookieValue (name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ){
        log.info(LogMessage.METHOD_API_CALLED.getMessage(MethodsHTTP.DELETE, Utils.getMethodName()));

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
