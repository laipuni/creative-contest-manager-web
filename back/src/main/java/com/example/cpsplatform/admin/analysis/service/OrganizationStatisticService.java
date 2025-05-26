package com.example.cpsplatform.admin.analysis.service;

import com.example.cpsplatform.admin.analysis.repository.OrganizationStatisticRepository;
import com.example.cpsplatform.admin.analysis.response.OrganizationDistributionDto;
import com.example.cpsplatform.admin.analysis.response.OrganizationDistributionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrganizationStatisticService {

    private final OrganizationStatisticRepository organizationRepository;

    public OrganizationDistributionResponse getOrganizationDistribution() {
        List<OrganizationDistributionDto> result = organizationRepository.getOrganizationStatistics();
        return OrganizationDistributionResponse.of(result);
    }
}
