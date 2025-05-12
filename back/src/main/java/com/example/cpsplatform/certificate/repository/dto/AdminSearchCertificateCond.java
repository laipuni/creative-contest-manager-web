package com.example.cpsplatform.certificate.repository.dto;

import com.example.cpsplatform.certificate.domain.CertificateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AdminSearchCertificateCond {
    private int page;
    private int pageSize;
    private String orderType;
    private String direction;
    private CertificateType certificateType;
    private String searchType;
    private String keyword;
}
