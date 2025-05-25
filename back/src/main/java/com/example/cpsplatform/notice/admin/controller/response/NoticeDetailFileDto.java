package com.example.cpsplatform.notice.admin.controller.response;

import com.example.cpsplatform.file.domain.File;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NoticeDetailFileDto {

    private Long fileId;
    private String fileName;

    public static NoticeDetailFileDto of(File file) {
        return NoticeDetailFileDto.builder()
                .fileId(file.getId())
                .fileName(file.getOriginalName())
                .build();
    }
}