package com.example.cpsplatform.notice.controller.response;

import com.example.cpsplatform.utils.PagingUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class UserNoticeSearchResponse {

    private int totalPage;
    private int page;
    private int firstPage;
    private int lastPage;
    private int size;
    private List<UserNoticeSearchDto> noticeSearchDtoList;

    public static UserNoticeSearchResponse of(Page<UserNoticeSearchDto> dtos){
        int firstPage = PagingUtils.getStartPage(dtos.getNumber(), dtos.getSize());
        int lastPage = PagingUtils.getEndPage(firstPage,dtos.getTotalPages());

        return UserNoticeSearchResponse.builder()
                .totalPage(dtos.getTotalPages())
                .page(dtos.getNumber())
                .firstPage(firstPage)
                .lastPage(lastPage)
                .size((int) dtos.getTotalElements())
                .noticeSearchDtoList(dtos.getContent())
                .build();
    }

}
