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

    public static AdminSearchCertificateDto of(final Certificate certificate){
        return AdminSearchCertificateDto.builder()
                .certificateId(certificate.getId())
                .title(certificate.getContest().getTitle())
                .teamName(certificate.getTeam().getName())
                .createdAt(certificate.getCreatedAt())
                .build();
    }
}
