package com.Foodie.booking_service.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {

    @NotNull(message = "Number of table was not be empty")
    @Positive(message = "Number of table must be positive")
    private Integer tableNumber;

    @NotNull(message = "Please specify count of guests")
    @Positive(message = "Guests must be positive")
    private Integer guests;

    private String description;

    @NotNull(message = "Please indicate the time for your reservation from")
    private LocalDateTime bookingFrom;

    @NotNull(message = "Please indicate the time for your reservation to")
    private LocalDateTime bookingTo;
}
