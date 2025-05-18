package com.example.cpsplatform.notice.admin.controller.response;

import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.notice.domain.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class NoticeDetailResponse {

    private Long noticeId;
    private String title;
    private Long viewCount;
    private String writer;
    private String writerEmail;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;
    private String content;
    private List<NoticeDetailFileDto> fileList;

    public static NoticeDetailResponse of(Notice notice, List<File> files){
        return NoticeDetailResponse.builder()
                .noticeId(notice.getId())
                .title(notice.getTitle())
                .viewCount(notice.getViewCount())
                .writer(notice.getWriter().getName())
                .writerEmail(notice.getWriter().getEmail())
                .createAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .content(notice.getContent())
                .fileList(
                        files.stream()
                                .map(NoticeDetailFileDto::of)
                                .toList()
                )
                .build();
    }

}
