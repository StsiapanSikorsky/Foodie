package com.Foodie.authentivation_service.advice;

import com.Foodie.authentivation_service.advice.exception.DataExistException;
import com.Foodie.authentivation_service.advice.exception.InvalidDataException;
import com.Foodie.authentivation_service.advice.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommandControllerAdvice {


    @ExceptionHandler
    protected ResponseEntity<String> handlerDataExistException(
            DataExistException e
    ){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler
    protected ResponseEntity<String> handlerInvalidDataException(
            InvalidDataException e
    ){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler
    protected ResponseEntity<String> handdlerNotFoundException(
            NotFoundException e
    ){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }
}
