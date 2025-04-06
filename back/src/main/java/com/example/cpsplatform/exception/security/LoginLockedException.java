package com.example.cpsplatform.exception.security;

import org.springframework.security.core.AuthenticationException;

public class LoginLockedException extends AuthenticationException {
    public LoginLockedException(final String msg) {
        super(msg);
    }

    public LoginLockedException() {
        super("로그인 실패가 누적되어 계정이 15분 동안 잠금 처리되었습니다. 잠시 후 다시 시도해주세요.");
    }
}
