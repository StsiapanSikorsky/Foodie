package com.Foodie.restaurant_service.dto;

import com.Foodie.restaurant_service.constants.RestaurantType;
import com.Foodie.restaurant_service.constants.RestaurantWeekends;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDto {
    private Integer id;
    private String restaurantName;
    private String description;
    private String city;
    private String address;
    private RestaurantType type;
    private LocalTime workFromAtWeekend;
    private LocalTime workToAtWeekend;
    private LocalTime workFromAtWorkday;
    private LocalTime workToAtWorkday;
    private List<RestaurantWeekends> restaurantWeekends;
    private List<String> imageUrls;
    private List<RestaurantTableDto> tables;
}