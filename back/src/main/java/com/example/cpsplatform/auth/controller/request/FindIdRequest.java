package com.example.cpsplatform.auth.controller.request;

import com.example.cpsplatform.member.service.dto.FindIdDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FindIdRequest {

    @NotBlank(message = "인증 정보는 필수입니다.")
    private String recipient;

    @NotBlank(message = "인증 코드는 필수입니다.")
    private String authCode;

    @NotBlank(message = "전송 수단 선택은 필수입니다.")
    private String senderType;

    public FindIdDto toFindIdDto(){
        return new FindIdDto(recipient,authCode,senderType);
    }

}
