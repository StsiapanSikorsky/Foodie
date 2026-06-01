package com.Foodie.booking_service.services;

import com.Foodie.booking_service.dto.BookingDto;
import com.Foodie.booking_service.request.BookingRequest;
import com.Foodie.booking_service.response.BookingResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface UserBookingService {

    BookingResponse<BookingDto> createBooking(
            @NotNull Integer restaurantId,
            @NotNull @Valid BookingRequest bookingRequest,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            HttpServletResponse response
    );

    BookingResponse<BookingDto> getBookingById();

    BookingResponse<BookingDto> getMyBooking();

    void softDeleteBooking();

    BookingResponse<BookingDto> updateBooking();


}
