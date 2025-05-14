package com.example.cpsplatform.notice.admin.controller.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NoticeModifyResponse {

    private boolean isSuccess;
    private String message;
    private Long noticeId;

    public static NoticeModifyResponse of(boolean isSuccess, String message, Long noticeId){
        return NoticeModifyResponse.builder()
                .isSuccess(isSuccess)
                .message(message)
                .noticeId(noticeId)
                .build();
    }

}
