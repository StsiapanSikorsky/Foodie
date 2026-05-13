package com.Foodie.restaurant_service.advice.exceptions.advice;

import com.Foodie.restaurant_service.advice.exceptions.*;
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
    protected ResponseEntity<String> handleDataExistsException(
            DataExistsException e
    ){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler
    protected ResponseEntity<String> handleInvalidDataException(
            InvalidDataException e
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
    protected ResponseEntity<String> handleUnauthorizedException(
            UnauthorizedException e
    ){
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(e.getMessage());
    }
}
