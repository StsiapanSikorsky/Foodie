package com.Foodie.restaurant_service.service.impl;

import com.Foodie.restaurant_service.advice.exceptions.*;
import com.Foodie.restaurant_service.controllers.feignAuthenticationService.AuthServiceClient;
import com.Foodie.restaurant_service.dto.RestaurantDto;
import com.Foodie.restaurant_service.entity.Restaurant;
import com.Foodie.restaurant_service.mapper.RestaurantMapper;
import com.Foodie.restaurant_service.repository.RestaurantRepository;
import com.Foodie.restaurant_service.repository.criteria.RestaurantSearchCriteria;
import com.Foodie.restaurant_service.request.RestaurantRequest;
import com.Foodie.restaurant_service.request.restaurants.SearchRestaurantRequest;
import com.Foodie.restaurant_service.request.restaurants.UpdateRestaurantRequest;
import com.Foodie.restaurant_service.responce.PaginationResponce;
import com.Foodie.restaurant_service.responce.RestaurantResponce;
import com.Foodie.restaurant_service.responce.authentication.AuthenticationValidationResponse;
import com.Foodie.restaurant_service.service.RestaurantService;
import com.Foodie.restaurant_service.utils.ErrorMessage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper mapper;
    private final AuthServiceClient authServiceClient;

    @Override
    public RestaurantResponce<RestaurantDto> getRestaurantById(
            @NotNull Integer restaurantId
    ) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage(restaurantId)));

        return RestaurantResponce.createSuccessful(mapper.toRestaurantDto(restaurant));
    }

    @Override
    public RestaurantResponce<RestaurantDto> addNewRestaurant(
           @NotNull RestaurantRequest request,
           @NotNull String jwtToken
    ) {
        if(restaurantRepository.existsByRestaurantName(request.getRestaurantName()))
        {
            throw new DataExistsException(ErrorMessage.RESTAURANT_EXISTS_BY_NAME.getMessage(request.getRestaurantName()));
        }

        AuthenticationValidationResponse validation = authServiceClient.validateToken(jwtToken);
        List<String> roles = validation.getRoles();
        if (roles == null || (!roles.contains("OWNER") && !roles.contains("ADMIN"))) {
            throw new IncorrectRoleException("Only owners can create restaurants");
        }
        if(!validation.isValid()){
            throw new InvalidDataException("Invalid token");
        }

        Restaurant restaurant = mapper.restaurantRequestToRestaurant(request);
        restaurant.setOwnerId(validation.getUserId());
        restaurant = restaurantRepository.save(restaurant);

        return RestaurantResponce.createSuccessful(mapper.toRestaurantDto(restaurant));
    }

    @Override
    public RestaurantResponce<RestaurantDto> updateRestaurant(
            @NotNull Integer restaurantId,
            @NotNull @Valid UpdateRestaurantRequest request,
            @NotNull String jwtToken
    ) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage(restaurantId)));

        AuthenticationValidationResponse validation = authServiceClient.validateToken(jwtToken);
        if (!validation.isValid()) {
            throw new UnauthorizedException("Invalid or expired token");
        }
        boolean isOwner = restaurant.getOwnerId().equals(validation.getUserId());
        boolean isAdmin = validation.getRoles() != null && validation.getRoles().contains("ADMIN");
        if (!isOwner && !isAdmin) {
            throw new IncorrectRoleException("You don't have permission to delete this restaurant");
        }

        Restaurant updatableRestaurant = mapper.updateRestaurantRequestToRestaurant(restaurant, request);
        updatableRestaurant.setUpdated(LocalDateTime.now());
        restaurantRepository.save(updatableRestaurant);

        RestaurantDto updatedRestaurantDto = mapper.toRestaurantDto(updatableRestaurant);

        return RestaurantResponce.createSuccessful(updatedRestaurantDto);
    }

    @Override
    public void softDeleteRestaurant(
            @NotNull Integer restaurantId,
            @NotNull String jwtToken
    ) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage(restaurantId)));

        AuthenticationValidationResponse validation = authServiceClient.validateToken(jwtToken);
        if (!validation.isValid()) {
            throw new UnauthorizedException("Invalid or expired token");
        }
        boolean isOwner = restaurant.getOwnerId().equals(validation.getUserId());
        boolean isAdmin = validation.getRoles() != null && validation.getRoles().contains("ADMIN");
        if (!isOwner && !isAdmin) {
            throw new IncorrectRoleException("You don't have permission to delete this restaurant");
        }

        restaurant.setDeleted(true);
        restaurantRepository.save(restaurant);
    }

    @Override
    public RestaurantResponce<PaginationResponce<RestaurantDto>> getAllRestaurants(
            @NotNull Pageable pageable
    ) {
        Page<RestaurantDto> restaurants = restaurantRepository.findAll(pageable)
                .map(mapper::toRestaurantDto);

        PaginationResponce<RestaurantDto> response = new PaginationResponce<>(
                restaurants.getContent(),
                new PaginationResponce.Pagination(
                        restaurants.getTotalElements(),
                        pageable.getPageSize(),
                        restaurants.getNumber() + 1,
                        restaurants.getTotalPages()
                )
        );

        return RestaurantResponce.createSuccessful(response);
    }

    @Override
    public RestaurantResponce<PaginationResponce<RestaurantDto>> searchRestaurants(
            @NotNull SearchRestaurantRequest request,
            @NotNull Pageable pageable
    ) {
        Specification<Restaurant> specification = new RestaurantSearchCriteria(request);
        Page<RestaurantDto> restaurantsAfterFiltration = restaurantRepository.findAll(specification, pageable)
                .map(mapper::toRestaurantDto);

        PaginationResponce<RestaurantDto> response = new PaginationResponce<>(
                restaurantsAfterFiltration.getContent(),
                new PaginationResponce.Pagination(
                    restaurantsAfterFiltration.getTotalElements(),
                    pageable.getPageSize(),
                    restaurantsAfterFiltration.getNumber() + 1,
                    restaurantsAfterFiltration.getTotalPages()
                )
        );
        return RestaurantResponce.createSuccessful(response);
    }
}
