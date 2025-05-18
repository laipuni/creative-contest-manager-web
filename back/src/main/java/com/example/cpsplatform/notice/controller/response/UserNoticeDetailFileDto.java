package com.example.cpsplatform.notice.controller.response;

import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.notice.admin.controller.response.NoticeDetailFileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserNoticeDetailFileDto {

    private Long fileId;
    private String fileName;

    public static UserNoticeDetailFileDto of(File file) {
        return UserNoticeDetailFileDto.builder()
                .fileId(file.getId())
                .fileName(file.getOriginalName())
                .build();
    }

}
