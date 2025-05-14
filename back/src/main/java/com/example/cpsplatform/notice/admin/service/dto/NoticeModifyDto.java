package com.example.cpsplatform.notice.admin.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@AllArgsConstructor
public class NoticeModifyDto {
    private Long noticeId;
    private String username;
    private String title;
    private String content;
    private List<Long> deleteFileIds;
}
