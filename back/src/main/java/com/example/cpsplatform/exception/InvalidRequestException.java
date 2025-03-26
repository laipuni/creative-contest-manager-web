package com.example.cpsplatform.exception;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
