package com.example.cpsplatform.exception;

public class CryptoException extends RuntimeException {
    public CryptoException(String message) {
        super("암호화 처리 중 문제가 발생했습니다.");
    }

    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }

    public CryptoException(final Throwable cause) {
        super(cause);
    }
}
