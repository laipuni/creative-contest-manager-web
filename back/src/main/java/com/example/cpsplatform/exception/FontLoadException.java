package com.example.cpsplatform.exception;

import java.io.IOException;

// 폰트 로딩 관련 예외
public class FontLoadException extends IllegalStateException {
    public FontLoadException(String message) {
        super(message);
    }

    public FontLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
