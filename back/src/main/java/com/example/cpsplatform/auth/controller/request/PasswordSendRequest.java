package com.example.cpsplatform.auth.controller.request;

import com.example.cpsplatform.member.service.dto.PasswordResetCodeDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordSendRequest {

    @NotBlank(message = "비밀번호를 찾을 아이디는 필수입니다.")
    private String loginId;

    @NotBlank(message = "인증 정보는 필수입니다.")
    private String recipient;

    @NotBlank(message = "전송 수단 선택은 필수입니다.")
    private String senderType;

    public PasswordSendRequest(final String loginId, final String recipient, final String senderType) {
        this.loginId = loginId;
        this.recipient = recipient;
        this.senderType = senderType;
    }

    public PasswordResetCodeDto toPasswordResetCodeDto(){
        return new PasswordResetCodeDto(loginId,recipient,senderType);
    }
}
