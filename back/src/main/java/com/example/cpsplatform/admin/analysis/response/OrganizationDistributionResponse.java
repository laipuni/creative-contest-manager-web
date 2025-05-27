package com.example.cpsplatform.admin.analysis.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OrganizationDistributionResponse {

    private List<OrganizationDistributionDto> distributionList;

    public static OrganizationDistributionResponse of(List<OrganizationDistributionDto> dtos){
        //type에 맞는 이름 설정
        dtos.forEach(OrganizationDistributionDto::setDescription);
        return OrganizationDistributionResponse.builder()
                .distributionList(dtos)
                .build();
    }

}
