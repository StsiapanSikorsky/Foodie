package com.Foodie.booking_service.services;

import com.Foodie.booking_service.dto.BookingDto;
import com.Foodie.booking_service.request.BookingRequest;
import com.Foodie.booking_service.request.UpdateBookingRequest;
import com.Foodie.booking_service.response.BookingResponse;
import com.Foodie.booking_service.response.PaginationResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;

public interface BookingService {

    BookingResponse<BookingDto> createBooking(
            @NotNull Integer restaurantId,
            @NotNull @Valid BookingRequest bookingRequest,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            HttpServletResponse response
    );

    BookingResponse<BookingDto> getBookingById(
            @NotNull Long bookingId
    );

    BookingResponse<PaginationResponse<BookingDto>> getUserBookings(
            @NotNull Pageable pageable,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            HttpServletResponse response
    );

    BookingResponse<PaginationResponse<BookingDto>> getOwnerBookings(
            @NotNull Pageable pageable,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            HttpServletResponse response
    );

    BookingResponse<BookingDto> updateBooking(
            @NotNull Long bookingId,
            @NotNull UpdateBookingRequest updateBookingRequest,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            HttpServletResponse response
    );

    void softDeleteBooking();
}
