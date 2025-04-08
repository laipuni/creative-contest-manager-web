package com.example.cpsplatform.exception;

public class DuplicateDataException extends RuntimeException {
    public DuplicateDataException(final String message) {
        super(message);
    }

    public DuplicateDataException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
