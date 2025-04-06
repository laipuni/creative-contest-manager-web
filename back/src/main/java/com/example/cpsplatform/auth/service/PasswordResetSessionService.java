package com.example.cpsplatform.auth.service;

public interface PasswordResetSessionService {

    public String storePasswordResetSession(final String loginId);

    public void confirmPasswordResetSession(final String loginId, final String session);

}
