package com.example.cpsplatform.certificate.repository;


import com.example.cpsplatform.certificate.controller.response.SearchCertificateDto;
import com.example.cpsplatform.certificate.controller.response.SearchCertificateResponse;
import com.example.cpsplatform.certificate.domain.CertificateType;

import java.util.List;

public interface CertificateRepositoryCustom {

    public SearchCertificateResponse SearchCertificate(final int page, final int pageSize, final String order, final CertificateType certificateType, final String username);

}
