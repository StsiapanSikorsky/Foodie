package com.Foodie.restaurant_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantTableDto{

    private Long id;
    private Integer restaurantId;
    private Integer numberOfTable;
    private String description;
    private Integer capacity;
}
