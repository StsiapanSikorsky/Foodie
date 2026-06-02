package com.Foodie.booking_service.response.restaurant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantCheckResponse {

    boolean owner;
    Integer restaurantId;
    Integer numberOfTable;
    Integer guests;
}
