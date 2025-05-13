package com.example.cpsplatform.certificate.repository;

import com.example.cpsplatform.certificate.domain.Certificate;
import com.example.cpsplatform.certificate.domain.CertificateType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate,Long>, CertificateRepositoryCustom {

    @EntityGraph(attributePaths = {"member", "team", "contest"})
    Optional<Certificate> findById(Long certificatedId);

    void deleteAllByTeamId(Long teamId);

    void deleteAllByTeam_IdInAndCertificateType(List<Long> teamIds, CertificateType certificateType);

    List<Certificate> findAllByContestId(Long contestId);
}
