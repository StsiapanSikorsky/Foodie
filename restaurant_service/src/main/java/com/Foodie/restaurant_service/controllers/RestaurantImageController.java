package com.Foodie.restaurant_service.controllers;

import com.Foodie.restaurant_service.responce.RestaurantResponse;
import com.Foodie.restaurant_service.service.RestaurantImageService;
import jakarta.servlet.http.HttpServletResponse;
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
public class RestaurantImageController {

    private final RestaurantImageService restaurantImageService;

    @PostMapping
    public ResponseEntity<RestaurantResponse<List<String>>> uploadImages(
            @PathVariable (name = "id") Integer id,
            @RequestParam ("files") List<MultipartFile> files,
            @CookieValue (name = "Authorization", required = false) String jwtToken,
            @CookieValue (name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ){
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
    public ResponseEntity<Void> deleteImage(
            @PathVariable (name = "id") Integer id,
            @RequestParam String imageUrl,
            @CookieValue (name = "Authorization", required = false) String jwtToken,
            @CookieValue (name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ){
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
