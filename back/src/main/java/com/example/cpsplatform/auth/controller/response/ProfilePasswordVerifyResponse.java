package com.example.cpsplatform.auth.controller.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfilePasswordVerifyResponse {

    private String session;

    public static ProfilePasswordVerifyResponse of(final String session) {
        return ProfilePasswordVerifyResponse.builder()
                .session(session)
                .build();
    }
}
