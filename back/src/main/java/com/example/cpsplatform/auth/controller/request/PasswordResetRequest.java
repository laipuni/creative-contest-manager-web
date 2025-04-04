package com.example.cpsplatform.auth.controller.request;

import com.example.cpsplatform.auth.service.dto.PasswordResetDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordResetRequest {

    @NotBlank(message = "유효하지 않는 접근입니다.")
    private String session;

    @NotBlank(message = "비밀번호를 재설정 할 아이디는 필수입니다.")
    private String loginId;

    @Size(min = 4, max = 8, message = "비밀번호는 4-8자 이내여야 합니다")
    private String resetPassword;

    @NotBlank(message = "비밀번호확인은 필수입니다")
    private String confirmPassword;

    public PasswordResetDto toResetPasswordDto(){
        return new PasswordResetDto(session,loginId,resetPassword,confirmPassword);
    }

}
