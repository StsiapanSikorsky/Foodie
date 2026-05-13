package com.Foodie.restaurant_service.request.restaurants;

import com.Foodie.restaurant_service.constants.RestaurantType;
import com.Foodie.restaurant_service.constants.RestaurantWeekends;
import com.Foodie.restaurant_service.entity.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchRestaurantRequest implements Serializable {

    private String restaurantName;
    private String city;
    private String address;

    private RestaurantType type;

    private String keyword;
}
