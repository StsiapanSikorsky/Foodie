package com.Foodie.booking_service.dto;

import com.Foodie.booking_service.enums.BookingStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class BookingDto implements Serializable {
    private Long id;
    private Integer userId;
    private Integer restaurantId;
    private Integer tableNumber;
    private Integer guests;
    private BookingStatus status;
    private String description;
    private LocalDateTime bookingFrom;
    private LocalDateTime bookingTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
