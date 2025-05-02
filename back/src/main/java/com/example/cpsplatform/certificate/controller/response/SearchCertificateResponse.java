package com.example.cpsplatform.certificate.controller.response;

import com.example.cpsplatform.PagingUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

import static com.example.cpsplatform.contest.admin.service.ContestAdminService.CONTEST_PAGE_SIZE;

@Getter
@Builder
@AllArgsConstructor
public class SearchCertificateResponse {

    private int totalPage;
    private int page;
    private int firstPage;
    private int lastPage;
    private int size;
    private List<SearchCertificateDto> certificateDtoList;

    public static SearchCertificateResponse of(Page<SearchCertificateDto> certificateDtoPage){
        int firstPage = PagingUtils.getStartPage(certificateDtoPage.getNumber(), CONTEST_PAGE_SIZE);
        int lastPage = PagingUtils.getEndPage(firstPage,certificateDtoPage.getTotalPages());

        return SearchCertificateResponse.builder()
                .totalPage(certificateDtoPage.getTotalPages())
                .page(certificateDtoPage.getNumber())
                .firstPage(firstPage)
                .lastPage(lastPage)
                .size((int) certificateDtoPage.getTotalElements())
                .certificateDtoList(certificateDtoPage.getContent())
                .build();
    }


}
