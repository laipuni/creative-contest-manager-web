package com.example.cpsplatform.exception;

public class ClientRequestException extends RuntimeException{

    public ClientRequestException(final String message) {
        super(message);
    }

    public ClientRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
