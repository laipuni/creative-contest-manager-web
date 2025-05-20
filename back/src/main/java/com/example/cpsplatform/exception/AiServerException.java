package com.example.cpsplatform.exception;

public class AiServerException extends RuntimeException{

    private final int status;

    public AiServerException(final String message, final int status) {
        super(message);
        this.status = status;
    }

    public AiServerException(final String message, int status, final Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
