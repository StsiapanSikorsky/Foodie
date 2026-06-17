package com.Foodie.booking_service.services.impl;

import com.Foodie.booking_service.dto.BookingDto;
import com.Foodie.booking_service.entity.Booking;
import com.Foodie.booking_service.enums.CacheKeyPrefix;
import com.Foodie.booking_service.enums.ErrorMessage;
import com.Foodie.booking_service.mapper.BookingMapper;
import com.Foodie.booking_service.response.PaginationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class CacheService {

    private final RedisTemplate<String, BookingDto> redisTemplate;
    private final RedisTemplate<String, PaginationResponse<BookingDto>> paginationRedisTemplate;

    private final BookingMapper bookingMapper;

    public Optional<BookingDto> findById(
            Long bookingId
    ){
        try {
            BookingDto cachedValue = redisTemplate.opsForValue().get(CacheKeyPrefix.BOOKING.getPrefix() + bookingId);
            if (cachedValue != null)
                return Optional.of(cachedValue);
        }
        catch (Exception e){
            log.error(ErrorMessage.REDIS_ERROR.getMessage(e.getMessage()));
        }
        return Optional.empty();
    }

    public Optional<PaginationResponse<BookingDto>> findUserPaginationBookings(
            Integer userId,
            Pageable pageable
    ){
        String cachedKey = buildBookingUserCacheKey(userId, pageable);
        try {
            PaginationResponse<BookingDto> cachedPaginationBookingDto = paginationRedisTemplate.opsForValue().get(cachedKey);
            if(cachedPaginationBookingDto != null)
                return Optional.of(cachedPaginationBookingDto);
        }
        catch (Exception e){
            log.error(ErrorMessage.REDIS_PAGINATION_ERROR.getMessage(e.getMessage()));
        }
        return Optional.empty();
    }

    public Optional<PaginationResponse<BookingDto>> findOwnerPaginationBookings(
            Integer ownerId,
            Integer restaurantId,
            Pageable pageable
    ){
        String cachedKey = buildBookingOwnerCacheKey(ownerId, restaurantId, pageable);
        try {
            PaginationResponse<BookingDto> cachedPaginationBookingDto = paginationRedisTemplate.opsForValue().get(cachedKey);
            if(cachedPaginationBookingDto != null)
                return Optional.of(cachedPaginationBookingDto);
        }
        catch (Exception e){
            log.error(ErrorMessage.REDIS_PAGINATION_ERROR.getMessage(e.getMessage()));
        }
        return Optional.empty();
    }

    public void saveBookingDto(
            Long bookingId,
            Booking booking
    ){
        try {
            redisTemplate.opsForValue().set(
                    CacheKeyPrefix.BOOKING.getPrefix() + bookingId,
                    bookingMapper.toBookingDto(booking),
                    5,
                    TimeUnit.MINUTES
            );
        }
        catch (Exception e){
            log.error(ErrorMessage.REDIS_SAVE_ERROR.getMessage(e.getMessage()));
        }
    }

    public void savePaginationBookingDto(
            String cachedKey,
            PaginationResponse<BookingDto> paginationBookingDto
    ){
        try {
            paginationRedisTemplate.opsForValue().set(
                    cachedKey,
                    paginationBookingDto,
                    5,
                    TimeUnit.MINUTES
            );
        }
        catch (Exception e){
            log.error(ErrorMessage.REDIS_SAVE_ERROR.getMessage(e.getMessage()));
        }
    }

    public void deleteBooking(
            Long bookingId
    ){
        redisTemplate.delete(CacheKeyPrefix.BOOKING.getPrefix() + bookingId);
    }

    protected static String buildBookingUserCacheKey(
            Integer userId,
            Pageable pageable
    ){
        return String.format(CacheKeyPrefix.BOOKING_WITH_PAGINATION.getPrefix(),
                userId,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
    }

    protected static String buildBookingOwnerCacheKey(
            Integer userId,
            Integer restaurantId,
            Pageable pageable
    ){
        return String.format(CacheKeyPrefix.BOOKING_WITH_PAGINATION.getPrefix(),
                userId,
                restaurantId,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
    }
}
