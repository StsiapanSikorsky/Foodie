package com.Foodie.restaurant_service.service;

import com.Foodie.restaurant_service.responce.RestaurantResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RestaurantImageService {

    RestaurantResponse<List<String>> uploadRestaurantImage(
            @NotNull Integer restaurantId,
            @NotNull List<MultipartFile> multipartFile,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            HttpServletResponse response
    );

    void deleteImage(
            @NotNull Integer restaurantId,
            @NotNull String imageUrl,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            HttpServletResponse response
    );
}
