package com.example.cpsplatform.certificate.repository;

import com.example.cpsplatform.certificate.domain.Certificate;
import com.example.cpsplatform.certificate.domain.CertificateType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate,Long>, CertificateRepositoryCustom {

    @EntityGraph(attributePaths = {"member", "team", "contest"})
    Optional<Certificate> findById(Long certificatedId);

    void deleteAllByTeamId(Long teamId);

    void deleteAllByTeam_IdInAndCertificateType(List<Long> teamIds, CertificateType certificateType);

    //소프트 딜리트된 contest는 조회가 안되므로 네이티브 쿼리로 조회
    @Query(value = "select * from certificate where preliminary_contest_id = :contestId",nativeQuery = true)
    List<Certificate> findAllByContestIdNative(@Param("contestId") Long contestId);
}
