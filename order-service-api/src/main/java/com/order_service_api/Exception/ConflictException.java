package com.order_service_api.Exception;

public class ConflictException extends RuntimeException{
    public ConflictException(String message){
        super(message);
    }
}
