package com.product_service_api.Exceptions;

public class ConflictException extends RuntimeException{
    public ConflictException(String message){
        super(message);
    }
}
