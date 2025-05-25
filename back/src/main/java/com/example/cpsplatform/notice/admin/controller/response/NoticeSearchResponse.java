package com.example.cpsplatform.notice.admin.controller.response;

import com.example.cpsplatform.PagingUtils;
import com.example.cpsplatform.certificate.repository.dto.AdminSearchCertificateDto;
import com.example.cpsplatform.certificate.repository.dto.AdminSearchCertificateResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class NoticeSearchResponse {

    private int totalPage;
    private int page;
    private int firstPage;
    private int lastPage;
    private int size;
    private List<NoticeSearchDto> noticeSearchDtoList;

    public static NoticeSearchResponse of(Page<NoticeSearchDto> dtos){
        int firstPage = PagingUtils.getStartPage(dtos.getNumber(), dtos.getSize());
        int lastPage = PagingUtils.getEndPage(firstPage,dtos.getTotalPages());

        return NoticeSearchResponse.builder()
                .totalPage(dtos.getTotalPages())
                .page(dtos.getNumber())
                .firstPage(firstPage)
                .lastPage(lastPage)
                .size((int) dtos.getTotalElements())
                .noticeSearchDtoList(dtos.getContent())
                .build();
    } 
    
}
