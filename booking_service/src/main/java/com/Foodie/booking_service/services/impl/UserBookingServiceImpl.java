package com.Foodie.booking_service.services.impl;

import com.Foodie.booking_service.advice.exception.BookingConflictException;
import com.Foodie.booking_service.advice.exception.NotFoundException;
import com.Foodie.booking_service.controllers.feignRestaurantService.RestaurantServiceClient;
import com.Foodie.booking_service.dto.BookingDto;
import com.Foodie.booking_service.entity.Booking;
import com.Foodie.booking_service.enums.BookingStatus;
import com.Foodie.booking_service.mapper.BookingMapper;
import com.Foodie.booking_service.repository.BookingRepository;
import com.Foodie.booking_service.request.BookingRequest;
import com.Foodie.booking_service.response.BookingResponse;
import com.Foodie.booking_service.response.authentication.AuthenticationValidationResponse;
import com.Foodie.booking_service.response.restaurant.RestaurantCheckResponse;
import com.Foodie.booking_service.services.UserBookingService;
import com.Foodie.booking_service.utils.ErrorMessage;
import com.Foodie.booking_service.utils.Utils;
import feign.FeignException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserBookingServiceImpl implements UserBookingService {

    private final BookingRepository bookingRepository;
    private final RestaurantServiceClient restaurantServiceClient;
    private final BookingMapper bookingMapper;
    private final Utils utils;

    @Override
    public BookingResponse<BookingDto> createBooking(
            @NotNull Integer restaurantId,
            @NotNull BookingRequest bookingRequest,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            HttpServletResponse response
    ) {
        AuthenticationValidationResponse validationResponse = utils.checkValidTokens(jwtToken, refreshToken, response);

        try {
            RestaurantCheckResponse checkRestaurantAndRoleRequest = restaurantServiceClient.getRestaurantIdAndCheckOwner(restaurantId, validationResponse.getUserId(), bookingRequest.getTableNumber());

            if(bookingRequest.getGuests() > checkRestaurantAndRoleRequest.getGuests())
                throw new BookingConflictException(ErrorMessage.GUESTS_CONFLICT.getMessage(checkRestaurantAndRoleRequest.getGuests()));
        }
        catch (FeignException.NotFound e){
            throw new NotFoundException(e.contentUTF8());
        }

        if(bookingRepository.existsConflictingBooking(restaurantId, bookingRequest.getTableNumber(), bookingRequest.getBookingFrom(), bookingRequest.getBookingTo()))
            throw new BookingConflictException(ErrorMessage.BOOKING_CONFLICT.getMessage());

        Booking newBooking = bookingMapper.bookingRequestToBooking(restaurantId, validationResponse.getUserId(), bookingRequest);
        newBooking.setStatus(BookingStatus.CREATE);
        bookingRepository.save(newBooking);

        return BookingResponse.createSuccessful(bookingMapper.toBookingDto(newBooking));
    }

    @Override
    public BookingResponse<BookingDto> getBookingById() {
        return null;
    }

    @Override
    public BookingResponse<BookingDto> getMyBooking() {
        return null;
    }

    @Override
    public void softDeleteBooking() {

    }

    @Override
    public BookingResponse<BookingDto> updateBooking() {
        return null;
    }
}
