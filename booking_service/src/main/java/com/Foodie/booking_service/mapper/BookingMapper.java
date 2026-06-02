package com.Foodie.booking_service.mapper;


import com.Foodie.booking_service.dto.BookingDto;
import com.Foodie.booking_service.entity.Booking;
import com.Foodie.booking_service.request.BookingRequest;
import com.Foodie.booking_service.request.UpdateBookingRequest;
import org.hibernate.type.descriptor.DateTimeUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = {DateTimeUtils.class, Object.class})
public interface BookingMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "restaurantId", source = "restaurantId")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Booking bookingRequestToBooking(Integer restaurantId, Integer userId, BookingRequest request);

    BookingDto toBookingDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "restaurantId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Booking updatedBookingRequestToBooking(@MappingTarget Booking booking, UpdateBookingRequest updateBookingRequest);
}
