package com.example.cpsplatform.auth.service;

public class InvalidPasswordResetSessionException extends RuntimeException {

    public InvalidPasswordResetSessionException() {
        super("세션이 만료되었습니다.");
    }

    public InvalidPasswordResetSessionException(final String message) {
        super(message);
    }
}
