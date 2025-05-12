package com.example.cpsplatform.certificate.controller.response;

import com.example.cpsplatform.certificate.domain.Certificate;
import com.example.cpsplatform.certificate.domain.CertificateType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class UserSearchCertificateDto {

    private Long certificateId;
    private String title;
    private CertificateType certificateType;
    private LocalDateTime createdAt;
    private String teamName;

    public static UserSearchCertificateDto of(final Certificate certificate){
        return UserSearchCertificateDto.builder()
                .certificateId(certificate.getId())
                .title(certificate.getContest().getTitle())
                .teamName(certificate.getTeam().getName())
                .createdAt(certificate.getCreatedAt())
                .build();
    }
}
