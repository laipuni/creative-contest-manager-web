package com.example.cpsplatform.exception;

public class LoginFailedException extends RuntimeException {
    public LoginFailedException(final String message) {
        super(message);
    }
    public LoginFailedException() {
        super("아이디 혹은 비밀번호가 일치하지 않습니다.");
    }
}
