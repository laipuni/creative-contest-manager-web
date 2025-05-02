package com.example.cpsplatform.certificate.repository;

import com.example.cpsplatform.certificate.domain.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<Certificate,Long> {
}
