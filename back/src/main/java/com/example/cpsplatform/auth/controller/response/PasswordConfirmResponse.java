package com.example.cpsplatform.auth.controller.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PasswordConfirmResponse {

    private String session;

    public static PasswordConfirmResponse of(final String session){
        return new PasswordConfirmResponse(session);
    }
}
