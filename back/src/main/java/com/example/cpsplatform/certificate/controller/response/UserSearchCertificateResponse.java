package com.example.cpsplatform.certificate.controller.response;

import com.example.cpsplatform.utils.PagingUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class UserSearchCertificateResponse {

    private int totalPage;
    private int page;
    private int firstPage;
    private int lastPage;
    private int size;
    private List<UserSearchCertificateDto> certificateDtoList;

    public static UserSearchCertificateResponse of(Page<UserSearchCertificateDto> certificateDtoPage){
        int firstPage = PagingUtils.getStartPage(certificateDtoPage.getNumber(), certificateDtoPage.getSize());
        int lastPage = PagingUtils.getEndPage(firstPage,certificateDtoPage.getTotalPages());

        return UserSearchCertificateResponse.builder()
                .totalPage(certificateDtoPage.getTotalPages())
                .page(certificateDtoPage.getNumber())
                .firstPage(firstPage)
                .lastPage(lastPage)
                .size((int) certificateDtoPage.getTotalElements())
                .certificateDtoList(certificateDtoPage.getContent())
                .build();
    }


}
