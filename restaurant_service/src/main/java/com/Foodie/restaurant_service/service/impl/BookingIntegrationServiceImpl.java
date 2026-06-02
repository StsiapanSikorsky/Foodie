package com.Foodie.restaurant_service.service.impl;

import com.Foodie.restaurant_service.advice.exceptions.NotFoundException;
import com.Foodie.restaurant_service.entity.Restaurant;
import com.Foodie.restaurant_service.repository.RestaurantRepository;
import com.Foodie.restaurant_service.repository.RestaurantTableRepository;
import com.Foodie.restaurant_service.responce.booking.RestaurantCheckResponse;
import com.Foodie.restaurant_service.service.BookingIntegrationService;
import com.Foodie.restaurant_service.utils.ErrorMessage;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingIntegrationServiceImpl implements BookingIntegrationService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableRepository tableRepository;

    @Override
    public RestaurantCheckResponse existRestaurantByIdAndCheckOwner(
            @NotNull Integer restaurantId,
            @NotNull Integer userId,
            @NotNull Integer numberOfTable
    ) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage(restaurantId)));

        if(!tableRepository.existsByRestaurantIdAndNumberOfTable(restaurantId ,numberOfTable))
            throw new NotFoundException(ErrorMessage.TABLE_NOT_FOUND.getMessage(numberOfTable, restaurantId));

        boolean isOwner = Objects.equals(restaurant.getOwnerId(), userId);

        return RestaurantCheckResponse.builder()
                .restaurantId(restaurant.getId())
                .owner(isOwner)
                .numberOfTable(numberOfTable)
                .guests(restaurant.getRestaurantTables().get(numberOfTable - 1).getCapacity())
                .build();
    }

    @Override
    public Integer getRestaurantIdWhenUserIsOwner(
            @NotNull Integer ownerId
    ) {
        Restaurant restaurant = restaurantRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.RESTAURANT_NOT_FOUND_WITH_OWNER_ID.getMessage(ownerId)));

        return restaurant.getId();
    }

    @Override
    public Boolean checkRestaurantOwner(
            @NotNull Integer restaurantId,
            @NotNull Integer userId
    ) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage(restaurantId)));

        return Objects.equals(restaurant.getOwnerId(), userId);
    }
}
