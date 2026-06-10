package com.Foodie.booking_service.advice;

import com.Foodie.booking_service.advice.exception.BookingConflictException;
import com.Foodie.booking_service.advice.exception.IncorrectDataException;
import com.Foodie.booking_service.advice.exception.IncorrectRoleException;
import com.Foodie.booking_service.advice.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class CommandControllerAdvice {

    @ExceptionHandler
    protected ResponseEntity<String> handleNotFoundException(
            NotFoundException e
    ){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler
    protected ResponseEntity<String> handleBookingConflictException(
            BookingConflictException e
    ){
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(e.getMessage());
    }

    @ExceptionHandler
    protected ResponseEntity<String> handleIncorrectRoleException(
            IncorrectRoleException e
    ){
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(e.getMessage());
    }

    @ExceptionHandler
    protected ResponseEntity<String> handleIIncorrectDataException(
            IncorrectDataException e
    ){
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(e.getMessage());
    }
}
