package com.product_service_api.Exceptions;

import com.product_service_api.DTO.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex){
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "Bad Request", LocalDateTime.now(), HttpStatusCode.valueOf(400));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex){
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "Forbidden", LocalDateTime.now(), HttpStatusCode.valueOf(403));
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex){
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "Conflict", LocalDateTime.now(), HttpStatusCode.valueOf(HttpStatus.CONFLICT.value()));
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleException(Exception ex){
        logger.error("Unhandled exception occurred", ex);
        ErrorResponse errorResponse = new ErrorResponse("Internal Server Error", "Error", LocalDateTime.now(), HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
