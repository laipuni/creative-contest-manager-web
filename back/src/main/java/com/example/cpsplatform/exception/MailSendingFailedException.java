package com.example.cpsplatform.exception;


public class MailSendingFailedException extends RuntimeException {

    public MailSendingFailedException(final String message) {
        super(message);
    }

    public MailSendingFailedException() {
        super("메일 전송 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }

    public MailSendingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}

