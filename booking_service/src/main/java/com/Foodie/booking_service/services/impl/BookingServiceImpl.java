package com.Foodie.booking_service.services.impl;

import com.Foodie.booking_service.advice.exception.BookingConflictException;
import com.Foodie.booking_service.advice.exception.IncorrectDataException;
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
import com.Foodie.booking_service.request.UpdateBookingRequest;
import com.Foodie.booking_service.response.BookingResponse;
import com.Foodie.booking_service.response.PaginationResponse;
import com.Foodie.booking_service.response.authentication.AuthenticationValidationResponse;
import com.Foodie.booking_service.response.restaurant.RestaurantCheckResponse;
import com.Foodie.booking_service.services.BookingService;
import com.Foodie.booking_service.enums.ErrorMessage;
import com.Foodie.booking_service.utils.AuthenticationUtils;
import com.Foodie.booking_service.enums.LogMessage;
import com.Foodie.booking_service.utils.Utils;
import feign.FeignException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RestaurantServiceClient restaurantServiceClient;
    private final BookingMapper bookingMapper;
    private final AuthenticationUtils authenticationUtils;

    private final CacheService cacheService;

    @Override
    @Transactional
    public BookingResponse<BookingDto> createBooking(
            @NotNull Integer restaurantId,
            @NotNull BookingRequest bookingRequest,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            HttpServletResponse response
    ) {
        validationTime(bookingRequest.getBookingFrom(), bookingRequest.getBookingTo());

        AuthenticationValidationResponse validationResponse = authenticationUtils.checkValidTokens(
                jwtToken,
                refreshToken,
                response
        );

        try {
            RestaurantCheckResponse checkRestaurantAndRoleRequest = restaurantServiceClient.getRestaurantIdAndCheckOwner(
                    restaurantId,
                    validationResponse.getUserId(),
                    bookingRequest.getTableNumber()
            );

            if(bookingRequest.getGuests() > checkRestaurantAndRoleRequest.getGuests())
            {
                log.warn(ErrorMessage.GUESTS_CONFLICT.getMessage(checkRestaurantAndRoleRequest.getGuests()) + Utils.getMethodName());
                throw new BookingConflictException(ErrorMessage.GUESTS_CONFLICT.getMessage(checkRestaurantAndRoleRequest.getGuests()));
            }
        }
        catch (FeignException.NotFound e){
            log.warn(e.contentUTF8() + Utils.getMethodName());
            throw new NotFoundException(e.contentUTF8());
        }

        if(bookingRepository.existsConflictingBooking(
                restaurantId,
                bookingRequest.getTableNumber(),
                bookingRequest.getBookingFrom(),
                bookingRequest.getBookingTo()))
        {
            log.warn(ErrorMessage.BOOKING_CONFLICT.getMessage()  + Utils.getMethodName());
            throw new BookingConflictException(ErrorMessage.BOOKING_CONFLICT.getMessage());
        }

        Booking newBooking = bookingMapper.bookingRequestToBooking(
                restaurantId,
                validationResponse.getUserId(),
                bookingRequest
        );
        newBooking.setStatus(BookingStatus.CREATED);
        bookingRepository.save(newBooking);
        log.info(LogMessage.USER_CREATE_NEW_BOOKING.getMessage(validationResponse.getUserId(), restaurantId, bookingRequest.getTableNumber(), bookingRequest.getGuests()) + Utils.getMethodName());

        return BookingResponse.createSuccessful(bookingMapper.toBookingDto(newBooking));
    }

    @Override
    public BookingResponse<BookingDto> getBookingById(
            @NotNull Long bookingId
    ) {
        Optional<BookingDto> resultFromCache = cacheService.findById(bookingId);
        if (resultFromCache.isPresent()){
            log.info(LogMessage.RESULT_RETURN_FROM_CACHE.getMessage() + Utils.getMethodName());
            return BookingResponse.createSuccessful(resultFromCache.get());
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn(ErrorMessage.BOOKING_NOT_FOUND_BY_ID.getMessage(bookingId) + Utils.getMethodName());
                    return new NotFoundException(ErrorMessage.BOOKING_NOT_FOUND_BY_ID.getMessage(bookingId));
                });

        cacheService.saveBookingDto(bookingId, booking);

        log.info(LogMessage.RESULT_RETURN_FROM_DB.getMessage() + Utils.getMethodName());
        return BookingResponse.createSuccessful(bookingMapper.toBookingDto(booking));
    }

    @Override
    public BookingResponse<PaginationResponse<BookingDto>> getUserBookings(
            @NotNull Pageable pageable,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            HttpServletResponse response
    ) {
        AuthenticationValidationResponse validationResponse = authenticationUtils.checkValidTokens(
                jwtToken,
                refreshToken,
                response
        );

        if(!validationResponse.getRoles().contains(UserRole.USER.getRole()))
        {
            log.warn(ErrorMessage.INCORRECT_ROLE.getMessage(validationResponse.getRoles()) + Utils.getMethodName());
            throw new IncorrectRoleException(ErrorMessage.INCORRECT_ROLE.getMessage(validationResponse.getRoles()));
        }

        Optional<PaginationResponse<BookingDto>> resultFromCache = cacheService.findUserPaginationBookings(
                validationResponse.getUserId(),
                pageable
        );
        if (resultFromCache.isPresent()){
            log.info(LogMessage.RESULT_RETURN_FROM_CACHE.getMessage() + Utils.getMethodName());
            return BookingResponse.createSuccessful(resultFromCache.get());
        }

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

        cacheService.savePaginationBookingDto(
                CacheService.buildBookingUserCacheKey(validationResponse.getUserId(), pageable),
                result
        );

        log.info(LogMessage.RESULT_RETURN_FROM_DB.getMessage() + Utils.getMethodName());
        return BookingResponse.createSuccessful(result);
    }

    @Override
    public BookingResponse<PaginationResponse<BookingDto>> getOwnerBookings(
            @NotNull Pageable pageable,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            HttpServletResponse response
    ) {
        AuthenticationValidationResponse validationResponse = authenticationUtils.checkValidTokens(
                jwtToken,
                refreshToken,
                response
        );

        if(!validationResponse.getRoles().contains(UserRole.OWNER.getRole())
                && !validationResponse.getRoles().contains(UserRole.ADMIN.getRole()))
        {
            log.warn(ErrorMessage.INCORRECT_ROLE.getMessage(validationResponse.getRoles()) + Utils.getMethodName());
            throw new IncorrectRoleException(ErrorMessage.INCORRECT_ROLE.getMessage(validationResponse.getRoles()));
        }

        Integer restaurantId = restaurantServiceClient.getRestaurantIdWhenUserIsOwner(validationResponse.getUserId());

        Optional<PaginationResponse<BookingDto>> resultFromCache = cacheService.findOwnerPaginationBookings(
                validationResponse.getUserId(),
                restaurantId,
                pageable
        );
        if(resultFromCache.isPresent()){
            log.info(LogMessage.RESULT_RETURN_FROM_CACHE.getMessage() + Utils.getMethodName());
            return BookingResponse.createSuccessful(resultFromCache.get());
        }

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

        cacheService.savePaginationBookingDto(
                CacheService.buildBookingOwnerCacheKey(validationResponse.getUserId(), restaurantId, pageable),
                result
        );

        log.info(LogMessage.RESULT_RETURN_FROM_DB.getMessage() + Utils.getMethodName());
        return BookingResponse.createSuccessful(result);
    }

    @Override
    @Transactional
    public BookingResponse<BookingDto> updateBooking(
            @NotNull Long bookingId,
            @NotNull @Valid UpdateBookingRequest updateBookingRequest,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            HttpServletResponse response
    ) {
        if(updateBookingRequest.getBookingTo() != null && updateBookingRequest.getBookingFrom() != null)
        {
            validationTime(updateBookingRequest.getBookingFrom(), updateBookingRequest.getBookingTo());
        }

        AuthenticationValidationResponse validationResponse = authenticationUtils.checkValidTokens(
                jwtToken,
                refreshToken,
                response
        );

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn(ErrorMessage.BOOKING_NOT_FOUND_BY_ID.getMessage(bookingId) + Utils.getMethodName());
                    return new NotFoundException(ErrorMessage.BOOKING_NOT_FOUND_BY_ID.getMessage(bookingId));
                });

        if (booking.getStatus() == BookingStatus.CANCELED) {
            log.warn(ErrorMessage.BOOKING_WAS_CANCELLED_BY_ID.getMessage(bookingId));
            throw new BookingConflictException(ErrorMessage.BOOKING_WAS_CANCELLED_BY_ID.getMessage(bookingId));
        }

        try {
            RestaurantCheckResponse checkRestaurantAndRoleRequest = restaurantServiceClient.getRestaurantIdAndCheckOwner(
                    booking.getRestaurantId(),
                    validationResponse.getUserId(),
                    updateBookingRequest.getTableNumber()
            );

            if(!booking.getUserId().equals(validationResponse.getUserId())
                    && !checkRestaurantAndRoleRequest.isOwner())
            {
                log.warn(ErrorMessage.DONT_HAVE_PERMISSION.getMessage() + Utils.getMethodName());
                throw new IncorrectRoleException(ErrorMessage.DONT_HAVE_PERMISSION.getMessage());
            }

            if(updateBookingRequest.getGuests() > checkRestaurantAndRoleRequest.getGuests())
            {
                log.warn(ErrorMessage.GUESTS_CONFLICT.getMessage(checkRestaurantAndRoleRequest.getGuests()) + Utils.getMethodName());
                throw new BookingConflictException(ErrorMessage.GUESTS_CONFLICT.getMessage(checkRestaurantAndRoleRequest.getGuests()));
            }
        }
        catch (FeignException.NotFound e){
            log.warn(e.contentUTF8() + Utils.getMethodName());
            throw new NotFoundException(e.contentUTF8());
        }

        if(bookingRepository.existsConflictingBookingExcludingId(
                booking.getRestaurantId(),
                updateBookingRequest.getTableNumber(),
                updateBookingRequest.getBookingFrom(),
                updateBookingRequest.getBookingTo(),
                bookingId))
        {
            log.warn(ErrorMessage.BOOKING_CONFLICT.getMessage() + Utils.getMethodName());
            throw new BookingConflictException(ErrorMessage.BOOKING_CONFLICT.getMessage());
        }

        Booking updatedBooking = bookingMapper.updatedBookingRequestToBooking(booking, updateBookingRequest);
        updatedBooking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(updatedBooking);
        cacheService.deleteBooking(bookingId);

        log.info(LogMessage.UPDATE_BOOKING_SUCCESS.getMessage(validationResponse.getUserId(), booking.getId()) + Utils.getMethodName());
        return BookingResponse.createSuccessful(bookingMapper.toBookingDto(updatedBooking));
    }

    @Override
    @Transactional
    public void softDeleteBooking(
            @NotNull Long bookingId,
            @NotNull String jwtToken,
            @NotNull String refreshToken,
            HttpServletResponse response
    ) {
        AuthenticationValidationResponse validationResponse = authenticationUtils.checkValidTokens(
                jwtToken,
                refreshToken,
                response
        );

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn(ErrorMessage.BOOKING_NOT_FOUND_BY_ID.getMessage(bookingId));
                    return new NotFoundException(ErrorMessage.BOOKING_NOT_FOUND_BY_ID.getMessage(bookingId));
                });

        Boolean isOwner = restaurantServiceClient.checkRestaurantOwner(booking.getRestaurantId(), validationResponse.getUserId());

        if(isOwner || validationResponse.getUserId().equals(booking.getUserId()))
        {
            booking.setStatus(BookingStatus.CANCELED);
            booking.setUpdatedAt(LocalDateTime.now());
            bookingRepository.save(booking);
            cacheService.deleteBooking(bookingId);
            log.info(LogMessage.BOOKING_STATUS_IS_CANCELLED.getMessage(BookingStatus.CANCELED));
        }
        else {
            log.warn(ErrorMessage.DONT_HAVE_PERMISSION.getMessage());
            throw new IncorrectRoleException(ErrorMessage.DONT_HAVE_PERMISSION.getMessage());
        }
    }

    private void validationTime(
            LocalDateTime timeFrom,
            LocalDateTime timeTo
    ){
        if(timeFrom == null || timeTo == null)
        {
            log.warn(ErrorMessage.TIME_UNDEFINED.getMessage() + Utils.getMethodName());
            throw new IncorrectDataException(ErrorMessage.TIME_UNDEFINED.getMessage());
        }

        if(timeTo.isEqual(timeFrom))
        {
            log.warn(ErrorMessage.TIME_IS_EQUALS.getMessage() + Utils.getMethodName());
            throw new IncorrectDataException(ErrorMessage.TIME_IS_EQUALS.getMessage());
        }

        if(timeTo.isBefore(timeFrom))
        {
            log.warn(ErrorMessage.END_TIME_CANNOT_BE_START_TIME.getMessage() + Utils.getMethodName());
            throw new IncorrectDataException(ErrorMessage.END_TIME_CANNOT_BE_START_TIME.getMessage());
        }

        if(timeFrom.isBefore(LocalDateTime.now().plusMinutes(15)))
        {
            log.warn(ErrorMessage.TIME_FROM_BEFORE_CURRENT_TIME.getMessage() + Utils.getMethodName());
            throw new IncorrectDataException(ErrorMessage.TIME_FROM_BEFORE_CURRENT_TIME.getMessage());
        }
    }
}