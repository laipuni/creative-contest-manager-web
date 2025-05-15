package com.example.cpsplatform.auth.service;

public class InvalidSessionException extends RuntimeException {

    public InvalidSessionException() {
        super("세션이 만료되었거나 유효하지 않습니다.");
    }

    public InvalidSessionException(final String message) {
        super(message);
    }
}
