package com.example.cpsplatform.auth.service.session;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SessionType {

    PASSWORD_RESET("Password_Session_",5L),
    PROFILE("Profile_Session_",5L);

    private final String key;// 세션 key
    private final Long sessionTimeout; // 세션 만료 시간

}
