package com.Foodie.restaurant_service.request.restaurants;

import com.Foodie.restaurant_service.constants.RestaurantType;
import com.Foodie.restaurant_service.constants.RestaurantWeekends;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantRequest {

    @NotBlank(message = "name is important")
    private String restaurantName;

    private String description;

    @NotBlank(message = "city is important")
    private String city;

    @NotBlank(message = "address is important")
    private String address;

    private RestaurantType type;

    private LocalTime workFromAtWeekend;

    private LocalTime workToAtWeekend;

    private LocalTime workFromAtWorkday;

    private LocalTime workToAtWorkday;

    private List<RestaurantWeekends> restaurantWeekends;
}
