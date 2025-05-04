package com.example.cpsplatform.certificate.admin.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteCertificateRequest {

    @NotNull(message = "삭제할 확인증의 정보는 필수 입니다.")
    private Long certificateId;

}
