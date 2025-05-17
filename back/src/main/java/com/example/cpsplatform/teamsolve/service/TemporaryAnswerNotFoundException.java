package com.example.cpsplatform.teamsolve.service;

public class TemporaryAnswerNotFoundException extends RuntimeException {

    public TemporaryAnswerNotFoundException() {
        super("최종 제출할 임시 저장한 답안이 없습니다.");
    }

    public TemporaryAnswerNotFoundException(final String message) {
        super(message);
    }

    public TemporaryAnswerNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
