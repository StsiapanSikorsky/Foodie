package com.Foodie.restaurant_service.service.impl;

import com.Foodie.restaurant_service.advice.exceptions.*;
import com.Foodie.restaurant_service.dto.RestaurantDto;
import com.Foodie.restaurant_service.entity.Restaurant;
import com.Foodie.restaurant_service.mapper.RestaurantMapper;
import com.Foodie.restaurant_service.repository.RestaurantRepository;
import com.Foodie.restaurant_service.repository.RestaurantTableRepository;
import com.Foodie.restaurant_service.repository.criteria.RestaurantSearchCriteria;
import com.Foodie.restaurant_service.request.restaurants.RestaurantRequest;
import com.Foodie.restaurant_service.request.restaurants.SearchRestaurantRequest;
import com.Foodie.restaurant_service.request.restaurants.UpdateRestaurantRequest;
import com.Foodie.restaurant_service.responce.PaginationResponse;
import com.Foodie.restaurant_service.responce.RestaurantResponse;
import com.Foodie.restaurant_service.responce.authentication.AuthenticationValidationResponse;
import com.Foodie.restaurant_service.service.RestaurantService;
import com.Foodie.restaurant_service.enums.ErrorMessage;
import com.Foodie.restaurant_service.enums.LogMessage;
import com.Foodie.restaurant_service.utils.Utils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableRepository tableRepository;
    private final RestaurantMapper mapper;
    private final Utils utils;

    @Override
    public RestaurantResponse<RestaurantDto> getRestaurantById(
            @NotNull Integer restaurantId
    ) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> {
                    log.warn(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage(restaurantId));
                    return new NotFoundException(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage(restaurantId));
                });

        return RestaurantResponse.createSuccessful(mapper.toRestaurantDto(restaurant));
    }

    @Override
    public RestaurantResponse<RestaurantDto> addNewRestaurant(
           @Valid @NotNull RestaurantRequest request,
           @NotNull String jwtToken,
           @NotNull String refreshToken,
           @NotNull HttpServletResponse response
    ) {
        if(restaurantRepository.existsByRestaurantName(request.getRestaurantName()))
        {
            log.warn(ErrorMessage.RESTAURANT_EXISTS_BY_NAME.getMessage(request.getRestaurantName()));
            throw new DataExistsException(ErrorMessage.RESTAURANT_EXISTS_BY_NAME.getMessage(request.getRestaurantName()));
        }

        AuthenticationValidationResponse validationResponse = utils.checkValidTokens(jwtToken, refreshToken, response);
        if(!utils.checkRole(validationResponse)){
            log.warn(ErrorMessage.USER_ROLE_HAS_NOT_VALID.getMessage());
            throw new IncorrectRoleException(ErrorMessage.USER_ROLE_HAS_NOT_VALID.getMessage());
        }

        Restaurant restaurant = mapper.restaurantRequestToRestaurant(request);
        restaurant.setOwnerId(validationResponse.getUserId());
        restaurant = restaurantRepository.save(restaurant);

        log.info(LogMessage.RESTAURANT_CREATE_SUCCESS.getMessage(validationResponse.getUserId(), restaurant.getId()));
        return RestaurantResponse.createSuccessful(mapper.toRestaurantDto(restaurant));
    }

    @Override
    public RestaurantResponse<RestaurantDto> updateRestaurant(
            @NotNull Integer restaurantId,
            @Valid @NotNull UpdateRestaurantRequest request,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            @NotNull HttpServletResponse response
    ) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> {
                    log.warn(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage(restaurantId));
                    return new NotFoundException(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage(restaurantId));
                });

        AuthenticationValidationResponse validationResponse = utils.checkValidTokens(jwtToken, refreshToken, response);
        if (!utils.isOwnerOrAdmin(restaurant, validationResponse)) {
            log.warn(ErrorMessage.INCORRECT_OWNER.getMessage());
            throw new IncorrectRoleException(ErrorMessage.INCORRECT_OWNER.getMessage());
        }

        Restaurant updatableRestaurant = mapper.updateRestaurantRequestToRestaurant(restaurant, request);
        updatableRestaurant.setUpdated(LocalDateTime.now());
        restaurantRepository.save(updatableRestaurant);
        RestaurantDto updatedRestaurantDto = mapper.toRestaurantDto(updatableRestaurant);

        log.info(LogMessage.RESTAURANT_UPDATE_SUCCESS.getMessage(validationResponse.getUserId(), updatableRestaurant.getId()));
        return RestaurantResponse.createSuccessful(updatedRestaurantDto);
    }

    @Override
    public void softDeleteRestaurant(
            @NotNull Integer restaurantId,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            @NotNull HttpServletResponse response
    ) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> {
                    log.warn(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage(restaurantId));
                    return new NotFoundException(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage(restaurantId));
                });

        AuthenticationValidationResponse validationResponse = utils.checkValidTokens(jwtToken, refreshToken, response);
        if (!utils.isOwnerOrAdmin(restaurant, validationResponse)) {
            log.warn(ErrorMessage.INCORRECT_OWNER.getMessage());
            throw new IncorrectRoleException(ErrorMessage.INCORRECT_OWNER.getMessage());
        }

        restaurant.setDeleted(true);
        restaurantRepository.save(restaurant);
        log.info(LogMessage.RESTAURANT_DELETE_SUCCESS.getMessage(validationResponse.getUserId(), restaurant.getId()));
    }

    @Override
    public RestaurantResponse<PaginationResponse<RestaurantDto>> getAllRestaurants(
            @NotNull Pageable pageable
    ) {
        Page<RestaurantDto> restaurants = restaurantRepository.findAll(pageable)
                .map(mapper::toRestaurantDto);

        PaginationResponse<RestaurantDto> response = new PaginationResponse<>(
                restaurants.getContent(),
                new PaginationResponse.Pagination(
                        restaurants.getTotalElements(),
                        pageable.getPageSize(),
                        restaurants.getNumber() + 1,
                        restaurants.getTotalPages()
                )
        );

        return RestaurantResponse.createSuccessful(response);
    }

    @Override
    public RestaurantResponse<PaginationResponse<RestaurantDto>> searchRestaurants(
            @NotNull SearchRestaurantRequest request,
            @NotNull Pageable pageable
    ) {
        Specification<Restaurant> specification = new RestaurantSearchCriteria(request);
        Page<RestaurantDto> restaurantsAfterFiltration = restaurantRepository.findAll(specification, pageable)
                .map(mapper::toRestaurantDto);

        PaginationResponse<RestaurantDto> response = new PaginationResponse<>(
                restaurantsAfterFiltration.getContent(),
                new PaginationResponse.Pagination(
                    restaurantsAfterFiltration.getTotalElements(),
                    pageable.getPageSize(),
                    restaurantsAfterFiltration.getNumber() + 1,
                    restaurantsAfterFiltration.getTotalPages()
                )
        );

        return RestaurantResponse.createSuccessful(response);
    }
}
