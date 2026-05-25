package com.Foodie.restaurant_service.mapper;

import com.Foodie.restaurant_service.dto.RestaurantTableDto;
import com.Foodie.restaurant_service.entity.Restaurant;
import com.Foodie.restaurant_service.entity.RestaurantTable;
import com.Foodie.restaurant_service.request.tables.TableRequest;
import com.Foodie.restaurant_service.request.tables.UpdateTableRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RestaurantTableMapper {

    @Mapping(target = "restaurantId", source = "restaurant.id")
    RestaurantTableDto toRestaurantTableDto(RestaurantTable restaurantTable);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "numberOfTable", source = "request.numberOfTable")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "capacity", source = "request.capacity")
    @Mapping(target = "restaurant", source = "restaurant")
    RestaurantTable tableRequestToRestaurantTable(TableRequest request, Restaurant restaurant);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "numberOfTable", source = "request.numberOfTable")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "capacity", source = "request.capacity")
    RestaurantTable updatedTableRequestToRestaurantTable(@MappingTarget RestaurantTable restaurantTable, UpdateTableRequest request);
}
