package com.example.cpsplatform.exception;

import java.io.IOException;

public class FileReadException extends IllegalStateException {

    public FileReadException(final String s) {
        super(s);
    }

    public FileReadException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
