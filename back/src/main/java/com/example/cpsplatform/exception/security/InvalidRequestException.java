package com.example.cpsplatform.exception.security;

import org.springframework.security.core.AuthenticationException;

public class InvalidRequestException extends AuthenticationException {
    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
