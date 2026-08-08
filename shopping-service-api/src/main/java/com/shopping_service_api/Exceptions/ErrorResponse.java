package com.shopping_service_api.Exceptions;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatusCode;

/**
 * Error response format aligned with Product Service API
 */
public class ErrorResponse {
    private String message;
    private String details;
    private LocalDateTime timestamp;
    private HttpStatusCode httpStatusCode;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String message, String details, LocalDateTime timestamp, HttpStatusCode httpStatusCode) {
        this.message = message;
        this.details = details;
        this.timestamp = timestamp;
        this.httpStatusCode = httpStatusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }
}

