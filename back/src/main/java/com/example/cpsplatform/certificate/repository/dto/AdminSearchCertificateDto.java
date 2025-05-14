package com.example.cpsplatform.certificate.repository.dto;

import com.example.cpsplatform.certificate.domain.Certificate;
import com.example.cpsplatform.certificate.domain.CertificateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@Builder
@AllArgsConstructor
public class AdminSearchCertificateDto {

    private Long certificateId;
    private String title;
    private CertificateType certificateType;
    private LocalDateTime createdAt;
    private String teamName;
    private String loginId;
    private String name;
    private int season;
}
