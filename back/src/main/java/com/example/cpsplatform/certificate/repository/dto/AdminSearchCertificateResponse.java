package com.example.cpsplatform.certificate.repository.dto;

import com.example.cpsplatform.PagingUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class AdminSearchCertificateResponse {
    private int totalPage;
    private int page;
    private int firstPage;
    private int lastPage;
    private int size;
    private List<AdminSearchCertificateDto> certificateDtoList;

    public static AdminSearchCertificateResponse of(Page<AdminSearchCertificateDto> certificateDtoPage){
        int firstPage = PagingUtils.getStartPage(certificateDtoPage.getNumber(), certificateDtoPage.getSize());
        int lastPage = PagingUtils.getEndPage(firstPage,certificateDtoPage.getTotalPages());

        return AdminSearchCertificateResponse.builder()
                .totalPage(certificateDtoPage.getTotalPages())
                .page(certificateDtoPage.getNumber())
                .firstPage(firstPage)
                .lastPage(lastPage)
                .size((int) certificateDtoPage.getTotalElements())
                .certificateDtoList(certificateDtoPage.getContent())
                .build();
    }
}
