package com.example.cpsplatform.exception.security;

import org.springframework.security.core.AuthenticationException;

public class SignupNotCompletedException extends AuthenticationException {
    public SignupNotCompletedException(final String message) {
        super(message);
    }
    public SignupNotCompletedException() {
        super("아직 회원가입 인증이 끝나지 않은 계정입니다.");
    }
}
