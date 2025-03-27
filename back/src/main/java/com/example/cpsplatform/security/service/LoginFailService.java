package com.example.cpsplatform.security.service;

public interface LoginFailService {

    public void failLogin(final String loginId);
    public Boolean isLockedAccount(final String loginId);

    public void successLogin(final String loginId);

}
