package com.example.cpsplatform.auth.controller.request;

import com.example.cpsplatform.auth.service.dto.PasswordConfirmDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordConfirmRequest {

    @NotBlank(message = "비밀번호를 찾을 아이디는 필수입니다.")
    private String loginId;

    @NotBlank(message = "인증 정보는 필수입니다.")
    private String recipient;

    @NotBlank(message = "전송 수단 선택은 필수입니다.")
    private String senderType;

    @NotBlank(message = "인증 코드는 필수입니다.")
    private String authCode;

    public PasswordConfirmRequest(final String loginId, final String recipient, final String senderType, final String authCode) {
        this.loginId = loginId;
        this.recipient = recipient;
        this.senderType = senderType;
        this.authCode = authCode;
    }

    public PasswordConfirmDto toPasswordConfirmDto(){
        return new PasswordConfirmDto(loginId,recipient,senderType,authCode);
    }
}
