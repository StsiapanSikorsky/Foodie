package com.Foodie.booking_service.services.impl;

import com.Foodie.booking_service.advice.exception.BookingConflictException;
import com.Foodie.booking_service.advice.exception.IncorrectRoleException;
import com.Foodie.booking_service.advice.exception.NotFoundException;
import com.Foodie.booking_service.controllers.feignRestaurantService.RestaurantServiceClient;
import com.Foodie.booking_service.dto.BookingDto;
import com.Foodie.booking_service.entity.Booking;
import com.Foodie.booking_service.enums.BookingStatus;
import com.Foodie.booking_service.enums.UserRole;
import com.Foodie.booking_service.mapper.BookingMapper;
import com.Foodie.booking_service.repository.BookingRepository;
import com.Foodie.booking_service.request.BookingRequest;
import com.Foodie.booking_service.response.BookingResponse;
import com.Foodie.booking_service.response.PaginationResponse;
import com.Foodie.booking_service.response.authentication.AuthenticationValidationResponse;
import com.Foodie.booking_service.response.restaurant.RestaurantCheckResponse;
import com.Foodie.booking_service.services.BookingService;
import com.Foodie.booking_service.utils.ErrorMessage;
import com.Foodie.booking_service.utils.AuthenticationUtils;
import feign.FeignException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RestaurantServiceClient restaurantServiceClient;
    private final BookingMapper bookingMapper;
    private final AuthenticationUtils authenticationUtils;

    @Override
    public BookingResponse<BookingDto> createBooking(
            @NotNull Integer restaurantId,
            @NotNull BookingRequest bookingRequest,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            HttpServletResponse response
    ) {
        AuthenticationValidationResponse validationResponse = authenticationUtils.checkValidTokens(jwtToken, refreshToken, response);

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
        newBooking.setStatus(BookingStatus.CREATED);
        bookingRepository.save(newBooking);

        return BookingResponse.createSuccessful(bookingMapper.toBookingDto(newBooking));
    }

    @Override
    public BookingResponse<BookingDto> getBookingById(
            @NotNull Long bookingId
    ) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.BOOKING_NOT_FOUND_BY_ID.getMessage(bookingId)));

        return BookingResponse.createSuccessful(bookingMapper.toBookingDto(booking));
    }

    @Override
    public BookingResponse<PaginationResponse<BookingDto>> getUserBookings(
            @NotNull Pageable pageable,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            HttpServletResponse response
    ) {
        AuthenticationValidationResponse validationResponse = authenticationUtils.checkValidTokens(jwtToken, refreshToken, response);

        if(!validationResponse.getRoles().contains(UserRole.USER.getRole()))
            throw new IncorrectRoleException(ErrorMessage.INCORRECT_ROLE.getMessage(validationResponse.getRoles()));

        Page<BookingDto> bookings = bookingRepository.findAllByUserId(validationResponse.getUserId(), pageable)
                .map(bookingMapper::toBookingDto);

        PaginationResponse<BookingDto> result = new PaginationResponse<>(
                bookings.getContent(),
                new PaginationResponse.Pagination(
                        bookings.getTotalElements(),
                        pageable.getPageSize(),
                        bookings.getNumber() + 1,
                        bookings.getTotalPages()
                )
        );

        return BookingResponse.createSuccessful(result);
    }

    @Override
    public BookingResponse<PaginationResponse<BookingDto>> getOwnerBookings(
            @NotNull Pageable pageable,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            HttpServletResponse response
    ) {
        AuthenticationValidationResponse validationResponse = authenticationUtils.checkValidTokens(jwtToken, refreshToken, response);

        if(!validationResponse.getRoles().contains(UserRole.OWNER.getRole()))
            throw new IncorrectRoleException(ErrorMessage.INCORRECT_ROLE.getMessage(validationResponse.getRoles()));

        Integer restaurantId = restaurantServiceClient.checkIsOwner(validationResponse.getUserId());

        Page<BookingDto> bookings = bookingRepository.findAllByRestaurantId(restaurantId, pageable)
                .map(bookingMapper::toBookingDto);

        PaginationResponse<BookingDto> result = new PaginationResponse<>(
                bookings.getContent(),
                new PaginationResponse.Pagination(
                        bookings.getTotalElements(),
                        pageable.getPageSize(),
                        bookings.getNumber() + 1,
                        bookings.getTotalPages()
                )
        );

        return BookingResponse.createSuccessful(result);
    }

    @Override
    public BookingResponse<BookingDto> updateBooking() {
        return null;
    }

    @Override
    public void softDeleteBooking() {

    }
}
