package com.example.cpsplatform.exception;

public class UnsupportedSenderTypeException extends RuntimeException {

    public UnsupportedSenderTypeException() {
        super("해당 전송 방법은 지원되지 않습니다. 다른 전송 수단을 시도해 주세요.");
    }

    public UnsupportedSenderTypeException(final String message) {
        super(message);
    }
}
