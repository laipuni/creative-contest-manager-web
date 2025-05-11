package com.example.cpsplatform.auth.strategy;


public class ProfileUpdateAuthCodeStrategy implements AuthCodeStrategy {

    public static final String PROFILE_UPDATE = "profile_update_";

    @Override
    public String createKey(final String key) {
        return PROFILE_UPDATE + key;
    }
}
