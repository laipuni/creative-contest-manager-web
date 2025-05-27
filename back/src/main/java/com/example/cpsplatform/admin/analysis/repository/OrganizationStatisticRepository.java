package com.example.cpsplatform.admin.analysis.repository;

import com.example.cpsplatform.admin.analysis.response.OrganizationDistributionDto;
import com.example.cpsplatform.member.domain.organization.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrganizationStatisticRepository extends JpaRepository<Organization,Long> {

    //유저의 타입에 따른 분포를 조회하는 쿼리
    //School, Company와 같은 자식 테이블로 구분하고, 학생 타입, 직장 타입으로 그룹으로 조회함
    @Query("""
        SELECT new com.example.cpsplatform.admin.analysis.response.OrganizationDistributionDto(
            CASE
                WHEN TYPE(o) = School THEN CAST(s.studentType AS string)
                WHEN TYPE(o) = Company THEN CAST(c.fieldType AS string)
            END,
            COUNT(o)
        )
        FROM Member m
        JOIN m.organization o
        LEFT JOIN School s ON TYPE(o) = School AND s.id = o.id
        LEFT JOIN Company c ON TYPE(o) = Company AND c.id = o.id
        GROUP BY TYPE(o),
            CASE
                WHEN TYPE(o) = School THEN CAST(s.studentType AS string)
                WHEN TYPE(o) = Company THEN CAST(c.fieldType AS string)
            END
        ORDER BY COUNT(o) DESC
    """)
    List<OrganizationDistributionDto> getOrganizationStatistics();

}
