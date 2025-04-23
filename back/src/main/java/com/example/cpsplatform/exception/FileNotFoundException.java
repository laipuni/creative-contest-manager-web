package com.example.cpsplatform.exception;

public class FileNotFoundException extends RuntimeException {

    public FileNotFoundException(final String message) {
        super(message);
    }

    public FileNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
