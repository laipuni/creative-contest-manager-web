package com.example.cpsplatform.notice.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class UserNoticeSearchDto {

    private Long noticeId;
    private String title;
    private Long viewCount;
    private String writer;
    private LocalDateTime createdAt;

}
