package com.Foodie.restaurant_service.service.impl;

import com.Foodie.restaurant_service.advice.exceptions.DataExistsException;
import com.Foodie.restaurant_service.advice.exceptions.IncorrectRoleException;
import com.Foodie.restaurant_service.advice.exceptions.NotFoundException;
import com.Foodie.restaurant_service.dto.RestaurantTableDto;
import com.Foodie.restaurant_service.entity.Restaurant;
import com.Foodie.restaurant_service.entity.RestaurantTable;
import com.Foodie.restaurant_service.mapper.RestaurantTableMapper;
import com.Foodie.restaurant_service.repository.RestaurantRepository;
import com.Foodie.restaurant_service.repository.RestaurantTableRepository;
import com.Foodie.restaurant_service.request.tables.TableRequest;
import com.Foodie.restaurant_service.request.tables.UpdateTableRequest;
import com.Foodie.restaurant_service.responce.PaginationResponse;
import com.Foodie.restaurant_service.responce.RestaurantTableResponse;
import com.Foodie.restaurant_service.responce.authentication.AuthenticationValidationResponse;
import com.Foodie.restaurant_service.service.RestaurantTableService;
import com.Foodie.restaurant_service.utils.ErrorMessage;
import com.Foodie.restaurant_service.utils.Utils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantTableServiceImpl implements RestaurantTableService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableMapper restaurantTableMapper;
    private final RestaurantTableRepository restaurantTableRepository;
    private final Utils utils;


    @Override
    public RestaurantTableResponse<RestaurantTableDto> addRestaurantTable(
            @NotNull Integer restaurantId,
            @Valid @NotNull TableRequest request,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            @NotNull HttpServletResponse response
    ) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage(restaurantId)));

        AuthenticationValidationResponse validationResponse = utils.checkValidTokens(jwtToken, refreshToken, response);

        if(!utils.isOwnerOrAdmin(restaurant, validationResponse)){
            throw new IncorrectRoleException(ErrorMessage.INCORRECT_OWNER.getMessage());
        }

        if(restaurantTableRepository.existsByRestaurantIdAndNumberOfTable(restaurantId, request.getNumberOfTable())){
            throw new DataExistsException(ErrorMessage.DUPLICATE_TABLE_EXCEPTION.getMessage(request.getNumberOfTable()));
        }

        RestaurantTable restaurantTable = restaurantTableMapper.tableRequestToRestaurantTable(request, restaurant);
        restaurantTableRepository.save(restaurantTable);
        RestaurantTableDto restaurantTableDto = restaurantTableMapper.toRestaurantTableDto(restaurantTable);
        return RestaurantTableResponse.createSuccessful(restaurantTableDto);
    }

    @Override
    public RestaurantTableResponse<PaginationResponse<RestaurantTableDto>> getAllTables(
            @NotNull Integer restaurantId,
            @NotNull Pageable pageable
    ) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage(restaurantId)));

        Page<RestaurantTable> page = restaurantTableRepository.findByRestaurantId(restaurantId, pageable);

        List<RestaurantTableDto> restaurantTableDtos = page.getContent().stream()
                .map(restaurantTableMapper::toRestaurantTableDto)
                .collect(Collectors.toList());

        PaginationResponse<RestaurantTableDto> paginationResponse = new PaginationResponse<>(
                restaurantTableDtos,
                new PaginationResponse.Pagination(
                        page.getTotalElements(),
                        page.getSize(),
                        page.getNumber() + 1,
                        page.getTotalPages()
                )
        );

        return RestaurantTableResponse.createSuccessful(paginationResponse);
    }

    @Override
    public RestaurantTableResponse<RestaurantTableDto> getTable(
            @NotNull Integer restaurantId,
            @NotNull Integer numberOfTable
    ) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage(restaurantId)));

        RestaurantTable restaurantTable = restaurantTableRepository.findByRestaurantIdAndNumberOfTable(restaurantId, numberOfTable)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.TABLE_NOT_FOUND.getMessage(numberOfTable, restaurantId)));

        RestaurantTableDto restaurantTableDto = restaurantTableMapper.toRestaurantTableDto(restaurantTable);

        return RestaurantTableResponse.createSuccessful(restaurantTableDto);
    }

    @Override
    public RestaurantTableResponse<RestaurantTableDto> updateTable(
            @NotNull Integer restaurantId,
            @NotNull Integer numberOfTable,
            @Valid @NotNull UpdateTableRequest request,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            HttpServletResponse response
    ) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage(restaurantId)));

        AuthenticationValidationResponse validationResponse = utils.checkValidTokens(jwtToken, refreshToken, response);

        if(!utils.isOwnerOrAdmin(restaurant, validationResponse)){
            throw new IncorrectRoleException(ErrorMessage.INCORRECT_OWNER.getMessage());
        }

        RestaurantTable restaurantTable = restaurantTableRepository.findByRestaurantIdAndNumberOfTable(restaurantId, numberOfTable)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.TABLE_NOT_FOUND.getMessage(numberOfTable, restaurantId)));

        RestaurantTable updatedRestaurantTable = restaurantTableMapper.updatedTableRequestToRestaurantTable(restaurantTable, request);
        restaurantTableRepository.save(updatedRestaurantTable);
        RestaurantTableDto restaurantTableDto = restaurantTableMapper.toRestaurantTableDto(updatedRestaurantTable);

        return RestaurantTableResponse.createSuccessful(restaurantTableDto);
    }

    @Override
    public void deleteTable(
            @NotNull Integer restaurantId,
            @NotNull Integer numberOfTable,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            HttpServletResponse response
    ) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.RESTAURANT_NOT_FOUND.getMessage(restaurantId)));

        AuthenticationValidationResponse validationResponse = utils.checkValidTokens(jwtToken, refreshToken, response);

        if(!utils.isOwnerOrAdmin(restaurant, validationResponse)){
            throw new IncorrectRoleException(ErrorMessage.INCORRECT_OWNER.getMessage());
        }

        RestaurantTable restaurantTable = restaurantTableRepository.findByRestaurantIdAndNumberOfTable(restaurantId, numberOfTable)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.TABLE_NOT_FOUND.getMessage(numberOfTable, restaurantId)));

        restaurantTableRepository.delete(restaurantTable);
    }
}
