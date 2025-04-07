package com.example.cpsplatform.auth.strategy;

public class SignUpVerifyStrategy implements AuthCodeStrategy{

    public static final String SIGN_UP_VERIFY = "signup_verified_";

    @Override
    public String createKey(final String key) {
        return SIGN_UP_VERIFY + key;
    }
}
