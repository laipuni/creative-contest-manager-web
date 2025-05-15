package com.example.cpsplatform.notice.admin.controller.request;

import com.example.cpsplatform.notice.admin.service.dto.NoticeModifyDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeModifyRequest {

    @NotNull(message = "수정할 공지사항의 정보는 필수입니다.")
    private Long noticeId;

    @NotBlank(message = "수정할 공지사항의 제목은 필수입니다.")
    private String title;

    @NotBlank(message = "수정할 공지사항의 내용은 필수입니다.")
    private String content;

    private List<Long> deleteFileIds;

    public NoticeModifyDto toNoticeModifyDto(String username){
        return new NoticeModifyDto(noticeId,username,title,content,deleteFileIds);
    }

}
