package com.example.cpsplatform.admin.analysis.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CityDistributionDto {

    private String city;
    private Long count;

}
