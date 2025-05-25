package com.example.cpsplatform.notice.admin.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeDeleteRequest {

    @NotNull(message = "삭제할 공지사항의 정보는 필수입니다.")
    private Long noticeId;

}
