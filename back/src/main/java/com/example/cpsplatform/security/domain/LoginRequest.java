package com.example.cpsplatform.security.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
public class LoginRequest {

    private String username;
    private String password;

}
