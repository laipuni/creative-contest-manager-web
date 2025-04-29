package com.example.cpsplatform.auth.strategy;

public class RegisterAuthCodeStrategy implements AuthCodeStrategy{

    private static final String REGISTER_PREFIX = "REGISTER_";

    @Override
    public String createKey(final String key) {
        return REGISTER_PREFIX + key;
    }
}
