package com.example.cpsplatform.certificate.repository;


import com.example.cpsplatform.certificate.controller.response.UserSearchCertificateResponse;
import com.example.cpsplatform.certificate.repository.dto.AdminSearchCertificateResponse;
import com.example.cpsplatform.certificate.repository.dto.AdminSearchCertificateCond;
import com.example.cpsplatform.certificate.repository.dto.UserSearchCertificateCond;

public interface CertificateRepositoryCustom {

    public UserSearchCertificateResponse SearchUserCertificate(UserSearchCertificateCond cond);
    public AdminSearchCertificateResponse SearchAdminCertificate(AdminSearchCertificateCond cond);
}
