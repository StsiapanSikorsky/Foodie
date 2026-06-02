package com.Foodie.booking_service.controllers;


import com.Foodie.booking_service.dto.BookingDto;
import com.Foodie.booking_service.request.BookingRequest;
import com.Foodie.booking_service.response.BookingResponse;
import com.Foodie.booking_service.response.PaginationResponse;
import com.Foodie.booking_service.services.BookingService;
import com.Foodie.booking_service.utils.AuthenticationUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("${end.point.booking}")
public class BookingController {

    private final BookingService bookingService;
    private final AuthenticationUtils authenticationUtils;

    @PostMapping("${end.point.restaurantId}")
    public ResponseEntity<BookingResponse<BookingDto>> createBooking(
            @PathVariable (name = "restaurantId") Integer restaurantId,
            @RequestBody @Valid BookingRequest bookingRequest,
            @CookieValue(name = "Authorization", required = false) String jwtToken,
            @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ){
        String checkedJwt = authenticationUtils.checkTokensInCookie(jwtToken, refreshToken, response);

        BookingResponse<BookingDto> result = bookingService.createBooking(
                restaurantId,
                bookingRequest,
                "Bearer " + checkedJwt,
                refreshToken,
                response
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(result);
    }

    @GetMapping("${end.point.bookingId}")
    public ResponseEntity<BookingResponse<BookingDto>> getBookingById(
            @PathVariable (name = "bookingId") Long bookingId
    ){
        BookingResponse<BookingDto> result = bookingService.getBookingById(bookingId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("${end.point.owner}")
    public ResponseEntity<BookingResponse<PaginationResponse<BookingDto>>> getAllOwnerBookings(
            @RequestParam (name = "page", defaultValue = "0") int page,
            @RequestParam (name = "limit", defaultValue = "10") int limit,
            @CookieValue (name = "Authorization", required = false) String jwtToken,
            @CookieValue (name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ){
        String checkedJwt = authenticationUtils.checkTokensInCookie(jwtToken, refreshToken, response);

        Pageable pageable = PageRequest.of(page, limit);
        BookingResponse<PaginationResponse<BookingDto>> result = bookingService.getOwnerBookings(
                pageable,
                "Bearer " + checkedJwt,
                refreshToken,
                response
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("${end.point.user}")
    public ResponseEntity<BookingResponse<PaginationResponse<BookingDto>>> getAllUserBookings(
            @RequestParam (name = "page", defaultValue = "0") int page,
            @RequestParam (name = "limit", defaultValue = "10") int limit,
            @CookieValue (name = "Authorization", required = false) String jwtToken,
            @CookieValue (name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ){
        String checkedJwt = authenticationUtils.checkTokensInCookie(jwtToken, refreshToken, response);

        Pageable pageable = PageRequest.of(page, limit);
        BookingResponse<PaginationResponse<BookingDto>> result = bookingService.getUserBookings(
                pageable,
                "Bearer " + checkedJwt,
                refreshToken,
                response
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }


}
