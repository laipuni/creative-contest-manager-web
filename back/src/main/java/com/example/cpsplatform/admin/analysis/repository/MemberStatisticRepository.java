package com.example.cpsplatform.admin.analysis.repository;


import com.example.cpsplatform.admin.analysis.response.CityDistributionDto;
import com.example.cpsplatform.admin.analysis.response.OrganizationDistributionDto;
import com.example.cpsplatform.admin.analysis.response.OrganizationDistributionResponse;
import com.example.cpsplatform.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberStatisticRepository extends JpaRepository<Member,Long> {


    //Member 엔티티에서 회원의 주소 정보를 기준으로,
    //도시별 회원 수를 집계한 후, 회원 수 기준으로 내림차순 정렬하여
    //CityDistributionDto(city, count) 형태의 리스트로 반환하는 쿼리입니다.
    @Query(""" 
            SELECT new com.example.cpsplatform.admin.analysis.response.CityDistributionDto(
                m.address.city,
                COUNT(m)
            )
            FROM Member m
            GROUP BY m.address.city
            ORDER BY COUNT(m) DESC
            """)
    List<CityDistributionDto> getMemberCityDistribution();
}
