package com.Foodie.booking_service.controllers;


import com.Foodie.booking_service.dto.BookingDto;
import com.Foodie.booking_service.enums.MethodsHTTP;
import com.Foodie.booking_service.request.BookingRequest;
import com.Foodie.booking_service.request.UpdateBookingRequest;
import com.Foodie.booking_service.response.BookingResponse;
import com.Foodie.booking_service.response.PaginationResponse;
import com.Foodie.booking_service.services.BookingService;
import com.Foodie.booking_service.utils.AuthenticationUtils;
import com.Foodie.booking_service.enums.LogMessage;
import com.Foodie.booking_service.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Booking controller")
public class BookingController {

    private final BookingService bookingService;
    private final AuthenticationUtils authenticationUtils;

    @PostMapping("${end.point.restaurantId}")
    @Operation(
            summary = "Создание бронирования"
    )
    public ResponseEntity<BookingResponse<BookingDto>> createBooking(
            @PathVariable (name = "restaurantId") Integer restaurantId,
            @RequestBody @Valid BookingRequest bookingRequest,
            @CookieValue(name = "Authorization", required = false) String jwtToken,
            @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ){
        log.info(LogMessage.METHOD_API_CALLED.getMessage(MethodsHTTP.POST, Utils.getMethodName()));

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
    @Operation(
            summary = "Получение бронирования по Id"
    )
    public ResponseEntity<BookingResponse<BookingDto>> getBookingById(
            @PathVariable (name = "bookingId") Long bookingId
    ){
        log.info(LogMessage.METHOD_API_CALLED.getMessage(MethodsHTTP.GET, Utils.getMethodName()));

        BookingResponse<BookingDto> result = bookingService.getBookingById(bookingId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("${end.point.owner}")
    @Operation(
            summary = "Получение списка бронирований определенного ресторана"
    )
    public ResponseEntity<BookingResponse<PaginationResponse<BookingDto>>> getAllOwnerBookings(
            @RequestParam (name = "page", defaultValue = "0") int page,
            @RequestParam (name = "limit", defaultValue = "10") int limit,
            @CookieValue (name = "Authorization", required = false) String jwtToken,
            @CookieValue (name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ){
        log.info(LogMessage.METHOD_API_CALLED.getMessage(MethodsHTTP.GET, Utils.getMethodName()));

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
    @Operation(
            summary = "Получение списка бронирований пользователя"
    )
    public ResponseEntity<BookingResponse<PaginationResponse<BookingDto>>> getAllUserBookings(
            @RequestParam (name = "page", defaultValue = "0") int page,
            @RequestParam (name = "limit", defaultValue = "10") int limit,
            @CookieValue (name = "Authorization", required = false) String jwtToken,
            @CookieValue (name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ){
        log.info(LogMessage.METHOD_API_CALLED.getMessage(MethodsHTTP.GET, Utils.getMethodName()));

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

    @PutMapping("${end.point.bookingId}")
    @Operation(
            summary = "Изменение бронирования"
    )
    public ResponseEntity<BookingResponse<BookingDto>> updateBooking(
            @PathVariable (name = "bookingId") Long bookingId,
            @RequestBody @Valid UpdateBookingRequest updateBookingRequest,
            @CookieValue (name = "Authorization", required = false) String jwtToken,
            @CookieValue (name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ){
        log.info(LogMessage.METHOD_API_CALLED.getMessage(MethodsHTTP.PUT, Utils.getMethodName()));

        String checkedJwt = authenticationUtils.checkTokensInCookie(jwtToken, refreshToken, response);

        BookingResponse<BookingDto> result = bookingService.updateBooking(
                bookingId,
                updateBookingRequest,
                "Bearer " + checkedJwt,
                refreshToken,
                response
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    @DeleteMapping("${end.point.bookingId}")
    @Operation(
            summary = "Перевод бронирования в статус CANCELED"
    )
    public ResponseEntity<Void> softDeleteBooking(
            @PathVariable (name = "bookingId") Long bookingId,
            @CookieValue (name = "Authorization", required = false) String jwtToken,
            @CookieValue (name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response
    ){
        log.info(LogMessage.METHOD_API_CALLED.getMessage(MethodsHTTP.DELETE, Utils.getMethodName()));

        String checkedJwt = authenticationUtils.checkTokensInCookie(jwtToken, refreshToken, response);

        bookingService.softDeleteBooking(
                bookingId,
                "Bearer " + checkedJwt,
                refreshToken,
                response
        );

        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }
}
