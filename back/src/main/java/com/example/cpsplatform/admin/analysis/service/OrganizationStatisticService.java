package com.example.cpsplatform.admin.analysis.service;

import com.example.cpsplatform.admin.analysis.repository.OrganizationStatisticRepository;
import com.example.cpsplatform.admin.analysis.response.OrganizationDistributionDto;
import com.example.cpsplatform.admin.analysis.response.OrganizationDistributionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrganizationStatisticService {

    public static final String ORGANIZATION_STATISTIC_LOG = "[ORGANIZATION_STATISTIC]";

    private final OrganizationStatisticRepository organizationRepository;

    public OrganizationDistributionResponse getOrganizationDistribution() {
        List<OrganizationDistributionDto> result = organizationRepository.getOrganizationStatistics();
        log.info("{} 전체 회원의 소속(학교, 기관 등)별 분포를 조회, 조회된 소속 분포 {}개",
                ORGANIZATION_STATISTIC_LOG,result.size());
        return OrganizationDistributionResponse.of(result);
    }
}
