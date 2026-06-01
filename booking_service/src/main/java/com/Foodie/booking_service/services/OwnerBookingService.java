package com.Foodie.booking_service.services;

import com.Foodie.booking_service.dto.BookingDto;
import com.Foodie.booking_service.response.BookingResponse;

public interface OwnerBookingService {

    BookingResponse<BookingDto> createBooking();

    BookingResponse<BookingDto> getAllBooking();

    void softDeleteBooking();

    BookingResponse<BookingDto> updateBooking();
}
