package com.Foodie.booking_service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingRequest {

    private Integer tableNumber;
    private Integer guests;
    private String description;
    private LocalDateTime bookingFrom;
    private LocalDateTime bookingTo;
}
