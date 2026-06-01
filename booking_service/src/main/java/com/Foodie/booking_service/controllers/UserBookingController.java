package com.Foodie.booking_service.controllers;


import com.Foodie.booking_service.dto.BookingDto;
import com.Foodie.booking_service.request.BookingRequest;
import com.Foodie.booking_service.response.BookingResponse;
import com.Foodie.booking_service.services.UserBookingService;
import com.Foodie.booking_service.utils.Utils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("${end.point.booking}")
public class UserBookingController {

    private final UserBookingService userBookingService;

    @PostMapping("${end.point.restaurantId}")
    public ResponseEntity<BookingResponse<BookingDto>> createBooking(
            @PathVariable (name = "restaurantId") Integer restaurantId,
            @RequestBody @Valid BookingRequest bookingRequest,
            @CookieValue(name = "Authorization", required = false) String jwtToken,
            @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ){
        Utils.checkTokensInCookie(jwtToken, refreshToken);

        BookingResponse<BookingDto> result = userBookingService.createBooking(
                restaurantId,
                bookingRequest,
                jwtToken,
                "Bearer " + refreshToken,
                response
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(result);
    }
}
