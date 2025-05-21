package com.example.cpsplatform.exception;

public class AiServerException extends RuntimeException{

    public AiServerException(final String message) {
        super(message);
    }

    public AiServerException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
