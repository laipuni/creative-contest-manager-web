package com.example.cpsplatform.certificate.repository.dto;

import com.example.cpsplatform.certificate.domain.CertificateType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSearchCertificateCond {
    private int page;
    private int pageSize;
    private String order;
    private CertificateType certificateType;
    private String username;
}
