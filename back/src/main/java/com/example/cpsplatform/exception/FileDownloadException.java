package com.example.cpsplatform.exception;

import java.io.IOException;

public class FileDownloadException extends IllegalStateException {

    public FileDownloadException(final String message) {
        super(message);
    }

    public FileDownloadException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
