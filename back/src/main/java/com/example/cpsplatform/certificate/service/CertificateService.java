package com.example.cpsplatform.certificate.service;

import com.example.cpsplatform.certificate.controller.response.SearchCertificateResponse;
import com.example.cpsplatform.certificate.domain.CertificateType;
import com.example.cpsplatform.certificate.repository.CertificateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CertificateService {

    public static final int CERTIFICATE_PAGE_SIZE = 10;

    private final CertificateRepository certificateRepository;

    public SearchCertificateResponse searchCertificates(final int page, final String order, final CertificateType certificateType, final String username) {
        return certificateRepository.SearchCertificate(page,CERTIFICATE_PAGE_SIZE,order,certificateType,username);
    }
}
