package com.example.cpsplatform.auth.service;

public class InvalidPasswordResetSessionException extends RuntimeException {

    public InvalidPasswordResetSessionException() {
        super("비밀번호 재설정 세션이 만료되었습니다.");
    }

    public InvalidPasswordResetSessionException(final String message) {
        super(message);
    }
}
