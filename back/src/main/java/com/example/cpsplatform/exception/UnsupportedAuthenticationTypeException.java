package com.example.cpsplatform.exception;

public class UnsupportedAuthenticationTypeException extends RuntimeException {
    public UnsupportedAuthenticationTypeException() {
        super("해당 인증 방법은 지원되지 않습니다. 다른 인증 수단을 시도해 주세요.");
    }

    public UnsupportedAuthenticationTypeException(final String message) {
        super(message);
    }
}
