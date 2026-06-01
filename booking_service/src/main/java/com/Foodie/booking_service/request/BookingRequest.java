package com.Foodie.booking_service.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {

    @NotNull(message = "Number of table was not be empty")
    private Integer tableNumber;

    @NotNull(message = "Please specify count of guests")
    private Integer guests;

    private String description;

    @NotNull(message = "Please indicate the time for your reservation from")
    private LocalDateTime bookingFrom;

    @NotNull(message = "Please indicate the time for your reservation to")
    private LocalDateTime bookingTo;
}
