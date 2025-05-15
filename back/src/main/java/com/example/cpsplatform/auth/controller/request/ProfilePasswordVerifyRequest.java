package com.example.cpsplatform.auth.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfilePasswordVerifyRequest {

    @NotBlank(message = "검증할 비밀번호는 필수입니다.")
    private String password;

}
