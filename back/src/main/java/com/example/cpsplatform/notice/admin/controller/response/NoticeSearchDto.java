package com.example.cpsplatform.notice.admin.controller.response;

import com.example.cpsplatform.notice.domain.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class NoticeSearchDto {

    private Long noticeId;
    private String title;
    private Long viewCount;
    private String writer;
    private LocalDateTime createdAt;

}
