package com.product_service_api.Exceptions;

import com.product_service_api.DTO.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex){
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "Bad Request", LocalDateTime.now(), HttpStatusCode.valueOf(400));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
