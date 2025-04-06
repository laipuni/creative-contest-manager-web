package com.example.cpsplatform.auth.service;

import com.example.cpsplatform.auth.generator.AuthCodeGenerator;
import com.example.cpsplatform.auth.sender.AuthCodeSender;
import com.example.cpsplatform.auth.storage.AuthCodeStorage;
import com.example.cpsplatform.auth.strategy.AuthCodeStrategy;
import com.example.cpsplatform.exception.AuthCodeMismatchException;
import com.example.cpsplatform.exception.UnsupportedAuthenticationTypeException;
import com.example.cpsplatform.exception.UnsupportedSenderTypeException;

import java.util.Map;

public class AuthService {

    private final Map<String,AuthCodeSender> authCodeSenderMap;
    private final AuthCodeStorage authCodeStorage;
    private final AuthCodeGenerator authCodeGenerator;
    private final Map<String, AuthCodeStrategy> strategyMap;

    public AuthService(final Map<String,AuthCodeSender> authCodeSenderMap, final AuthCodeStorage authCodeStorage, final Map<String, AuthCodeStrategy> strategyMap,final AuthCodeGenerator authCodeGenerator) {
        this.authCodeSenderMap = authCodeSenderMap;
        this.authCodeStorage = authCodeStorage;
        this.strategyMap = strategyMap;
        this.authCodeGenerator = authCodeGenerator;
    }

    public void sendAuthCode(final String recipient,final String senderType, final String strategyType){
        String key = getAuthCodeKey(recipient,strategyType);
        //key가 존재한다면 제거
        authCodeStorage.removeAuthCode(key);
        String authCode = authCodeGenerator.generateAuthCode();
        authCodeStorage.storeAuthCode(key,authCode);
        sendAuthCodeWith(recipient,senderType,authCode);
    }
    private String getAuthCodeKey(final String recipient, final String strategyType){
        AuthCodeStrategy authCodeStrategy = strategyMap.get(strategyType);
        if(authCodeStrategy == null){
            //해당 인증 전략이 존재하지 않는다면 예외 발생
            throw new UnsupportedAuthenticationTypeException();
        }
        return authCodeStrategy.createKey(recipient);
    }

    private void sendAuthCodeWith(final String recipient, final String senderType, final String authCode){
        AuthCodeSender authCodeSender = authCodeSenderMap.get(senderType);
        if(authCodeSender == null){
            //해당 전송 수단은 존재하지 않는다면 예외 발생
            throw new UnsupportedSenderTypeException();
        }
        authCodeSender.sendAuthCode(recipient,authCode);
    }

    public boolean verifyAuthCode(final String recipient,final String authCode, final String strategyType){
        AuthCodeStrategy authCodeStrategy = strategyMap.get(strategyType);
        if(authCodeStrategy == null){
            //해당 인증 수단은 존재하지 않는다면 예외 발생
            throw new UnsupportedAuthenticationTypeException();
        }
        String key = authCodeStrategy.createKey(recipient);
        String confirmAuthCode = authCodeStorage.findAuthCode(key);
        if(confirmAuthCode == null || !confirmAuthCode.equals(authCode)){
            //인증코드와 같지 않거나 해당 key의 authCode가 없을 경우 예외 발생
            throw new AuthCodeMismatchException();
        }

        //키가 일치할 경우 저장된 인증 코드 제거
        authCodeStorage.removeAuthCode(key);
        return true;
    }

}
