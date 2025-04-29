package com.example.cpsplatform.auth.controller.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FindIdResponse {

    private String loginId;

    @Builder
    private FindIdResponse(final String loginId) {
        this.loginId = loginId;
    }

    public static FindIdResponse of(final String loginId){
        return FindIdResponse.builder()
                .loginId(loginId)
                .build();
    }

}
