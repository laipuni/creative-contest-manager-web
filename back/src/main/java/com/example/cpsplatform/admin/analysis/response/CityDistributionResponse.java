package com.example.cpsplatform.admin.analysis.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CityDistributionResponse {

    private List<CityDistributionDto> cityDistributionDtoList;

    public static CityDistributionResponse of(final List<CityDistributionDto> dtos){
        return new CityDistributionResponse(dtos);
    }

}
