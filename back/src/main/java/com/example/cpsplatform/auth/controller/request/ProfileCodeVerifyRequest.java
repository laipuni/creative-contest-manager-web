package com.example.cpsplatform.auth.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileCodeVerifyRequest {

    @NotBlank(message = "인증 정보는 필수입니다.")
    private String recipient;

    @NotBlank(message = "인증코드는 필수입니다.")
    private String authCode;

    @NotBlank(message = "인증 수단은 필수입니다.")
    private String strategyType;

    public ProfileCodeVerifyRequest(final String recipient, final String authCode, final String strategyType) {
        this.recipient = recipient;
        this.authCode = authCode;
        this.strategyType = strategyType;
    }

}
