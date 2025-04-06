package com.example.cpsplatform.exception;

public class AuthCodeMismatchException extends RuntimeException {

    public AuthCodeMismatchException() {
        super("유효하지 않은 인증 코드 입니다.");
    }

    public AuthCodeMismatchException(final String message) {
        super(message);
    }
}
