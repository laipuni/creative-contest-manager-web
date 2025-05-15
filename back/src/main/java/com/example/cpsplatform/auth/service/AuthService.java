package com.example.cpsplatform.auth.service;

import com.example.cpsplatform.auth.generator.AuthCodeGenerator;
import com.example.cpsplatform.auth.sender.AuthCodeSender;
import com.example.cpsplatform.auth.storage.AuthCodeStorage;
import com.example.cpsplatform.auth.strategy.AuthCodeStrategy;
import com.example.cpsplatform.exception.AuthCodeMismatchException;
import com.example.cpsplatform.exception.UnsupportedAuthenticationTypeException;
import com.example.cpsplatform.exception.UnsupportedSenderTypeException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 인증 코드 발송, 검증 및 관리를 담당하는 서비스 클래스
 * 다양한 전송 수단과 인증 전략을 지원함
 * @throws UnsupportedAuthenticationTypeException 지원하지 않는 인증 전략일 경우
 * @throws UnsupportedSenderTypeException 지원하지 않는 전송 수단일 경우
 * @throws AuthCodeMismatchException 인증코드가 일치하지 않거나 존재하지 않을 경우
 */

@Slf4j
public class AuthService {

    public static final String AUTH_SERVICE_LOG = "[AuthService]";

    private final Map<String,AuthCodeSender> authCodeSenderMap;
    private final AuthCodeStorage authCodeStorage;
    private final AuthCodeGenerator authCodeGenerator;
    private final Map<String, AuthCodeStrategy> strategyMap;

    /**
     * AuthService 생성자
     *
     * @param authCodeSenderMap 인증코드 전송 수단 맵 (key: 전송유형, value: 전송 구현체)
     * @param authCodeStorage 인증코드 저장소(현재는 redis)
     * @param strategyMap 인증 전략 맵 (key: 전략유형, value: 전략 구현체)
     * @param authCodeGenerator 인증코드 생성기
     */
    public AuthService(final Map<String,AuthCodeSender> authCodeSenderMap, final AuthCodeStorage authCodeStorage, final Map<String, AuthCodeStrategy> strategyMap,final AuthCodeGenerator authCodeGenerator) {
        this.authCodeSenderMap = authCodeSenderMap;
        this.authCodeStorage = authCodeStorage;
        this.strategyMap = strategyMap;
        this.authCodeGenerator = authCodeGenerator;
    }

    /**
     * 인증코드를 생성하여 지정된 수신자에게 전송함
     * 기존에 발급된 인증코드가 있을 경우 제거 후 새로 발급
     *
     * @param recipient 수신자 정보 (이메일, 전화번호 등)
     * @param senderType 전송 수단 유형 (email, sms 등)
     * @param strategyType 인증 전략 유형 (register, password_reset 등)
     */
    public void sendAuthCode(final String recipient,final String senderType, final String strategyType){
        log.info("{} 인증코드 전송: 수신자='{}', 전송유형='{}', 전략='{}'", AUTH_SERVICE_LOG, recipient, senderType, strategyType);
        String key = getAuthCodeKey(recipient,strategyType);
        //key가 존재한다면 제거
        authCodeStorage.removeAuthCode(key);
        String authCode = authCodeGenerator.generateAuthCode();
        authCodeStorage.storeAuthCode(key,authCode);
        sendAuthCodeWith(recipient,senderType,authCode);
    }


    //인증 전략에 따른 인증코드 저장 키를 생성함
    private String getAuthCodeKey(final String recipient, final String strategyType){
        AuthCodeStrategy authCodeStrategy = strategyMap.get(strategyType);
        if(authCodeStrategy == null){
            //해당 인증 전략이 존재하지 않는다면 예외 발생
            throw new UnsupportedAuthenticationTypeException();
        }
        return authCodeStrategy.createKey(recipient);
    }


    //지정된 전송 수단을 사용하여 인증코드를 수신자에게 전송함
    private void sendAuthCodeWith(final String recipient, final String senderType, final String authCode){
        AuthCodeSender authCodeSender = authCodeSenderMap.get(senderType);
        if(authCodeSender == null){
            //해당 전송 수단은 존재하지 않는다면 예외 발생
            throw new UnsupportedSenderTypeException();
        }
        authCodeSender.sendAuthCode(recipient,authCode);
    }

    /**
     * 수신자가 입력한 인증코드의 유효성을 검증함
     * 검증 성공 시 저장된 인증코드는 삭제됨
     *
     * @param recipient 수신자 정보
     * @param authCode 검증할 인증코드
     * @param strategyType 인증 전략 유형
     */
    public boolean verifyAuthCode(final String recipient,final String authCode, final String strategyType){
        log.info("{} 인증코드 검증: 수신자='{}', 전략='{}'", AUTH_SERVICE_LOG, recipient, strategyType);
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


    /**
     * 회원가입 과정에서 인증코드를 검증하고, 성공 시 회원가입 완료 정보를 저장함
     *
     * @param recipient 수신자 정보
     * @param authCode 검증할 인증코드
     * @param strategyType 인증 전략 유형 (register : email or phone
     * @param storeStrategyType 해당 이메일/휴대폰 번호가 인증되었음을 시스템에 기록 유형
     */
    public void verifyContactCode(final String recipient, final String authCode, final String strategyType, final String storeStrategyType){
        log.info("{} 인증 정보 검증, 수신자='{}', 전략='{}'", AUTH_SERVICE_LOG, recipient, strategyType);
        verifyAuthCode(recipient,authCode,strategyType);
        AuthCodeStrategy authCodeStrategy = strategyMap.get(storeStrategyType);
        if(authCodeStrategy == null){
            //해당 인증 수단은 존재하지 않는다면 예외 발생
            throw new UnsupportedAuthenticationTypeException();
        }
        String key = authCodeStrategy.createKey(recipient);
        authCodeStorage.storeAuthCode(key,recipient);
    }

}
