package com.example.cpsplatform.auth.storage;

public interface AuthCodeStorage {

    public void storeAuthCode(final String key, final String authCode);
    public String findAuthCode(final String key);
    public void removeAuthCode(final String key);

}
