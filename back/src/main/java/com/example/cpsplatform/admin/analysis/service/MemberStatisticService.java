package com.example.cpsplatform.admin.analysis.service;

import com.example.cpsplatform.admin.analysis.repository.MemberStatisticRepository;
import com.example.cpsplatform.admin.analysis.response.CityDistributionDto;
import com.example.cpsplatform.admin.analysis.response.CityDistributionResponse;
import com.example.cpsplatform.admin.analysis.response.OrganizationDistributionResponse;
import com.example.cpsplatform.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberStatisticService {

    private final MemberStatisticRepository memberStatisticRepository;

    public CityDistributionResponse getCityOrganization() {
        List<CityDistributionDto> memberCityDistribution = memberStatisticRepository.getMemberCityDistribution();
        return CityDistributionResponse.of(memberCityDistribution);
    }
}
