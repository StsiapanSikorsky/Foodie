package com.Foodie.restaurant_service.responce.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantCheckResponse {

    boolean owner;
    Integer restaurantId;
    Integer numberOfTable;
    Integer guests;
}
