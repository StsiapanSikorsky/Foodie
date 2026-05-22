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
import com.Foodie.restaurant_service.responce.authentication.AuthenticationRefreshResponse;
import com.Foodie.restaurant_service.responce.authentication.AuthenticationValidationResponse;
import com.Foodie.restaurant_service.service.RestaurantService;
import com.Foodie.restaurant_service.utils.ErrorMessage;
import com.Foodie.restaurant_service.utils.Utils;
import feign.FeignException;
import jakarta.servlet.http.Cookie;
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
           @NotNull String jwtToken,
           @NotNull String refreshToken,
           @NotNull HttpServletResponse response
    ) {
        if(restaurantRepository.existsByRestaurantName(request.getRestaurantName()))
        {
            throw new DataExistsException(ErrorMessage.RESTAURANT_EXISTS_BY_NAME.getMessage(request.getRestaurantName()));
        }

        AuthenticationValidationResponse validationResponse = checkValidTokens(jwtToken, refreshToken, response);
        if(!checkRole(validationResponse)){
            throw new IncorrectRoleException(ErrorMessage.USER_ROLE_HAS_NOT_VALID.getMessage());
        }

        Restaurant restaurant = mapper.restaurantRequestToRestaurant(request);
        restaurant.setOwnerId(validationResponse.getUserId());
        restaurant = restaurantRepository.save(restaurant);

        return RestaurantResponce.createSuccessful(mapper.toRestaurantDto(restaurant));
    }

    @Override
    public RestaurantResponce<RestaurantDto> updateRestaurant(
            @NotNull Integer restaurantId,
            @NotNull @Valid UpdateRestaurantRequest request,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            @NotNull HttpServletResponse response
    ) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage(restaurantId)));

        AuthenticationValidationResponse validationResponse = checkValidTokens(jwtToken, refreshToken, response);
        if (!isOwnerOrAdmin(restaurant, validationResponse)) {
            throw new IncorrectRoleException(ErrorMessage.INCORRECT_OWNER.getMessage());
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
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            @NotNull HttpServletResponse response
    ) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage(restaurantId)));

        AuthenticationValidationResponse validationResponse = checkValidTokens(jwtToken, refreshToken, response);
        if (!isOwnerOrAdmin(restaurant, validationResponse)) {
            throw new IncorrectRoleException(ErrorMessage.INCORRECT_OWNER.getMessage());
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


    private AuthenticationValidationResponse checkValidTokens(
            String jwtToken,
            String refreshToken,
            HttpServletResponse response
    ){
        AuthenticationValidationResponse validationResponse;

        validationResponse = authServiceClient.validateToken(jwtToken);

        if (!validationResponse.isValid()){
            AuthenticationRefreshResponse refreshResponse = authServiceClient.refreshToken(refreshToken);
            String updatedJwt = "Bearer " + refreshResponse.getToken();
            validationResponse = authServiceClient.validateToken(updatedJwt);

            setCookie(response, refreshResponse);
        }
        return validationResponse;
    }

    public void setCookie(
            HttpServletResponse response,
            AuthenticationRefreshResponse refreshResponse
    ){
        Cookie authenticationCookie = Utils.createAuthenticationCookie(refreshResponse.getToken());
        Cookie refreshtokenCookie = Utils.creauteRefreshTokenCookie(refreshResponse.getRefreshToken());
        response.addCookie(authenticationCookie);
        response.addCookie(refreshtokenCookie);
    }

    public boolean checkRole(
            AuthenticationValidationResponse validationResponse
    ){
        List<String> roles = validationResponse.getRoles();
        if (roles == null || (!roles.contains("OWNER") && !roles.contains("ADMIN")))
            return false;
        else
            return true;
    }

    public boolean isOwnerOrAdmin(
            Restaurant restaurant,
            AuthenticationValidationResponse validationResponse
    ){
        boolean isOwner = restaurant.getOwnerId().equals(validationResponse.getUserId());
        boolean isAdmin = validationResponse.getRoles() != null && validationResponse.getRoles().contains("ADMIN");
        return isOwner || isAdmin;
    }
}
