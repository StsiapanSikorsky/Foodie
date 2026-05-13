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
    Integer id;
    String restaurantName;
    String description;
    String city;
    String address;
    RestaurantType type;
    LocalTime workFromAtWeekend;
    LocalTime workToAtWeekend;
    LocalTime workFromAtWorkday;
    LocalTime workToAtWorkday;
    List<RestaurantWeekends> restaurantWeekends;
}