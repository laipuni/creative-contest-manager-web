package com.example.cpsplatform.admin.analysis.service;

import com.example.cpsplatform.admin.analysis.repository.MemberStatisticRepository;
import com.example.cpsplatform.admin.analysis.response.CityDistributionDto;
import com.example.cpsplatform.admin.analysis.response.CityDistributionResponse;
import com.example.cpsplatform.admin.analysis.response.OrganizationDistributionResponse;
import com.example.cpsplatform.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberStatisticService {

    public static final String MEMBER_STATISTIC_LOG = "[MEMBER_STATISTIC]";


    private final MemberStatisticRepository memberStatisticRepository;

    public CityDistributionResponse getCityOrganization() {
        List<CityDistributionDto> memberCityDistribution = memberStatisticRepository.getMemberCityDistribution();
        log.info("{} 전체 회원의 거주 도시별 분포 조회 완료, 조회된 도시 수: {}",
                MEMBER_STATISTIC_LOG, memberCityDistribution.size());
        return CityDistributionResponse.of(memberCityDistribution);
    }
}
