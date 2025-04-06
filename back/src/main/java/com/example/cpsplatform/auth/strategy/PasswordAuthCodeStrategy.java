package com.example.cpsplatform.auth.strategy;

public class PasswordAuthCodeStrategy implements AuthCodeStrategy{

    public static final String PASSWORD_AUTH_CODE_PREFIX = "Password_auth_code_";

    @Override
    public String createKey(final String key) {
        return PASSWORD_AUTH_CODE_PREFIX + key;
    }
}
