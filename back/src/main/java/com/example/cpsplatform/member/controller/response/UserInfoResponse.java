package com.example.cpsplatform.member.controller.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfoResponse {

    private String loginId;
    private String name;

    public static UserInfoResponse of(final String loginId, final String name) {
        return UserInfoResponse.builder()
                .loginId(loginId)
                .name(name)
                .build();
    }
}
