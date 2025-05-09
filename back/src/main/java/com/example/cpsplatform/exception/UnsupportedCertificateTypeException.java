package com.example.cpsplatform.exception;

/**
 * 잠시동안 혹은 더이상 지원하지 않는 확인증에 대한 작업을 요구할 때 발생하는 예외
 */
public class UnsupportedCertificateTypeException extends IllegalStateException {

    public UnsupportedCertificateTypeException(final String s) {
        super(s);
    }

    public UnsupportedCertificateTypeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
