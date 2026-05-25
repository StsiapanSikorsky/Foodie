package com.Foodie.restaurant_service.mapper;

import com.Foodie.restaurant_service.dto.RestaurantDto;
import com.Foodie.restaurant_service.entity.Restaurant;
import com.Foodie.restaurant_service.request.RestaurantRequest;
import com.Foodie.restaurant_service.request.restaurants.UpdateRestaurantRequest;
import org.hibernate.type.descriptor.DateTimeUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = {DateTimeUtils.class, Object.class}
)
public interface RestaurantMapper {

    @Mapping(target = "tables", source = "restaurantTables")
    RestaurantDto toRestaurantDto(Restaurant restaurant);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    Restaurant restaurantRequestToRestaurant(RestaurantRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    Restaurant updateRestaurantRequestToRestaurant(@MappingTarget Restaurant restaurant, UpdateRestaurantRequest updateRestaurantRequest);
}
