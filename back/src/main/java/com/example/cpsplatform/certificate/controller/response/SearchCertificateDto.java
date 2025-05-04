package com.example.cpsplatform.certificate.controller.response;

import com.example.cpsplatform.certificate.domain.Certificate;
import com.example.cpsplatform.certificate.domain.CertificateType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class SearchCertificateDto {

    private Long certificateId;
    private String title;
    private CertificateType certificateType;
    private LocalDateTime createdAt;
    private String teamName;

    public static SearchCertificateDto of(final Certificate certificate){
        return SearchCertificateDto.builder()
                .certificateId(certificate.getId())
                .title(certificate.getContest().getTitle())
                .teamName(certificate.getTeam().getName())
                .createdAt(certificate.getCreatedAt())
                .build();
    }
}
