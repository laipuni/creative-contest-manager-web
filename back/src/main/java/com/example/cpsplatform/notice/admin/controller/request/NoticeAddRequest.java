package com.example.cpsplatform.notice.admin.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeAddRequest {

    @NotBlank(message = "공지사항의 제목은 필수입니다.")
    private String title;

    @NotBlank(message = "공지사항의 내용은 필수입니다.")
    private String content;

}
