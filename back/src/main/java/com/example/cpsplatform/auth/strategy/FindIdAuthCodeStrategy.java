package com.example.cpsplatform.auth.strategy;

public class FindIdAuthCodeStrategy implements AuthCodeStrategy{

    private static final String FIND_ID_PREFIX = "Find_Id_";

    @Override
    public String createKey(final String key) {
        return FIND_ID_PREFIX + key;
    }
}
