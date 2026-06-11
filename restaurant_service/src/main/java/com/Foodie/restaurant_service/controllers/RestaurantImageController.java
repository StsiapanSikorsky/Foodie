package com.Foodie.restaurant_service.controllers;

import com.Foodie.restaurant_service.enums.MethodsHTTP;
import com.Foodie.restaurant_service.responce.RestaurantResponse;
import com.Foodie.restaurant_service.service.RestaurantImageService;
import com.Foodie.restaurant_service.enums.LogMessage;
import com.Foodie.restaurant_service.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("${end.point.restaurant.id.images}")
@RequiredArgsConstructor
@Tag(name = "Restaurant Image Controller")
public class RestaurantImageController {

    private final RestaurantImageService restaurantImageService;

    @PostMapping
    @Operation(
            summary = "Загрузка изображения в профиль ресторана"
    )
    public ResponseEntity<RestaurantResponse<List<String>>> uploadImages(
            @PathVariable (name = "id") Integer id,
            @RequestParam ("files") List<MultipartFile> files,
            @CookieValue (name = "Authorization", required = false) String jwtToken,
            @CookieValue (name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ){
        log.info(LogMessage.METHOD_API_CALLED.getMessage(MethodsHTTP.POST, Utils.getMethodName()));

        RestaurantResponse<List<String>> result = restaurantImageService.uploadRestaurantImage(
            id,
            files,
            "Bearer " + jwtToken,
            refreshToken,
            response
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }


    @DeleteMapping
    @Operation(
            summary = "Удаление изображения из профиля ресторана и хранилища"
    )
    public ResponseEntity<Void> deleteImage(
            @PathVariable (name = "id") Integer id,
            @RequestParam String imageUrl,
            @CookieValue (name = "Authorization", required = false) String jwtToken,
            @CookieValue (name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ){
        log.info(LogMessage.METHOD_API_CALLED.getMessage(MethodsHTTP.DELETE, Utils.getMethodName()));

        restaurantImageService.deleteImage(
                id,
                imageUrl,
                "Bearer " + jwtToken,
                refreshToken,
                response
        );

        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }
}
