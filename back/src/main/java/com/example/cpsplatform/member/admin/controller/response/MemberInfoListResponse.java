package com.example.cpsplatform.member.admin.controller.response;

import com.example.cpsplatform.PagingUtils;
import com.example.cpsplatform.certificate.controller.response.UserSearchCertificateDto;
import com.example.cpsplatform.certificate.controller.response.UserSearchCertificateResponse;
import com.example.cpsplatform.member.admin.controller.response.MemberInfoListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class MemberInfoListResponse {

    private int totalPage;
    private int page;
    private int firstPage;
    private int lastPage;
    private int size;
    private List<MemberInfoListDto> memberInfos;

    public static MemberInfoListResponse of(Page<MemberInfoListDto> page){
        int firstPage = PagingUtils.getStartPage(page.getNumber(), page.getSize());
        int lastPage = PagingUtils.getEndPage(firstPage,page.getTotalPages());

        return MemberInfoListResponse.builder()
                .totalPage(page.getTotalPages())
                .page(page.getNumber())
                .firstPage(firstPage)
                .lastPage(lastPage)
                .size((int) page.getTotalElements())
                .memberInfos(page.getContent())
                .build();
    }

}
