package com.example.cpsplatform.notice.admin.controller.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NoticeAddResponse {

    private boolean isSuccess;
    private String message;
    private Long noticeId;

    public static NoticeAddResponse of(boolean isSuccess, String message, Long noticeId){
        return NoticeAddResponse.builder()
                .isSuccess(isSuccess)
                .message(message)
                .noticeId(noticeId)
                .build();
    }

}
