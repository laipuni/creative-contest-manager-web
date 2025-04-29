package com.example.cpsplatform.auth.sender;

public interface AuthCodeSender {

    public void sendAuthCode(final String recipient, final String authCode);

}
