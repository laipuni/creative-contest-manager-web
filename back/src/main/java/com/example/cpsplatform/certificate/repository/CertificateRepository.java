package com.example.cpsplatform.certificate.repository;

import com.example.cpsplatform.certificate.domain.Certificate;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate,Long>, CertificateRepositoryCustom {

    @EntityGraph(attributePaths = {"member", "team", "contest"})
    Optional<Certificate> findById(Long certificatedId);

    void deleteAllByTeamId(Long teamId);
}
