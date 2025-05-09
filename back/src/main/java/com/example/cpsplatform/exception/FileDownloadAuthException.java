package com.example.cpsplatform.exception;

/**
 * 유저가 파일을 다운로드 받을 권한이 없을 때 발생하는 예외
 */
public class FileDownloadAuthException extends RuntimeException {

    public FileDownloadAuthException(final String message) {
        super(message);
    }

    public FileDownloadAuthException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
