package com.Foodie.restaurant_service.request.restaurants;

import com.Foodie.restaurant_service.constants.RestaurantWeekends;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRestaurantRequest implements Serializable {

    private String restaurantName;
    private String description;
    private String city;
    private String address;

    private LocalTime workFromAtWeekend;
    private LocalTime workToAtWeekend;
    private LocalTime workFromAtWorkday;
    private LocalTime workToAtWorkday;

    private List<RestaurantWeekends> restaurantWeekends;
}
