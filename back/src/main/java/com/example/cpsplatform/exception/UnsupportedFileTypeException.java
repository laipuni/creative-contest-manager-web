package com.example.cpsplatform.exception;

public class UnsupportedFileTypeException extends RuntimeException {

    public UnsupportedFileTypeException(final String message) {
        super(message);
    }

    public UnsupportedFileTypeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
