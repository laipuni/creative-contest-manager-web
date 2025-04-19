package com.example.cpsplatform.exception;

public class ContestJoinException extends RuntimeException {
    public ContestJoinException(final String message) {
        super(message);
    }

    public ContestJoinException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
