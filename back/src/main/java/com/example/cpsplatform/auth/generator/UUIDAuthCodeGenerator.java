package com.example.cpsplatform.auth.generator;

import java.util.UUID;

public class UUIDAuthCodeGenerator implements AuthCodeGenerator {

    public static final int AUTHCODE_MIN_LENGTH = 0;
    public static final int AUTHCODE_MAX_LENGTH = 8;

    @Override
    public String generateAuthCode() {
        //UUID로 8자리 랜덤 인증 코드 생성
        return UUID.randomUUID().toString().substring(AUTHCODE_MIN_LENGTH,AUTHCODE_MAX_LENGTH);
    }
}
