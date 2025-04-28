package com.example.cpsplatform.auth.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegisterAuthCodeSendRequest {

    @NotBlank(message = "인증 정보는 필수입니다.")
    private String recipient;

    @NotBlank(message = "전송 수단 선택은 필수입니다.")
    private String senderType;

    @NotBlank(message = "인증 수단은 필수입니다.")
    private String strategyType;

    public RegisterAuthCodeSendRequest(final String recipient, final String senderType, final String strategyType) {
        this.recipient = recipient;
        this.senderType = senderType;
        this.strategyType = strategyType;
    }

}
