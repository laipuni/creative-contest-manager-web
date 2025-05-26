package com.example.cpsplatform.auth.strategy;

import com.example.cpsplatform.auth.strategy.AuthCodeStrategy;

public class ProfileUpdateVerifyStrategy implements AuthCodeStrategy {

    public static final String PROFILE_UPDATE_VERIFIED = "profile_update_verified_";

    @Override
    public String createKey(final String key) {
        return PROFILE_UPDATE_VERIFIED + key;
    }
}
