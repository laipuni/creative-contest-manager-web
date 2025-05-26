package com.example.cpsplatform.admin.analysis;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.admin.analysis.response.CityDistributionResponse;
import com.example.cpsplatform.admin.analysis.response.OrganizationDistributionResponse;
import com.example.cpsplatform.admin.analysis.service.MemberStatisticService;
import com.example.cpsplatform.admin.analysis.service.OrganizationStatisticService;
import com.example.cpsplatform.admin.annotaion.AdminLog;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/statistics")
public class DataAnalysisController {

    private final OrganizationStatisticService organizationStatisticService;
    private final MemberStatisticService memberStatisticService;


    @AdminLog
    @GetMapping("/organization")
    public ApiResponse<OrganizationDistributionResponse> getOrganizationDistribution(){
        OrganizationDistributionResponse response = organizationStatisticService.getOrganizationDistribution();
        return ApiResponse.ok(response);
    }

    @AdminLog
    @GetMapping("/members/city")
    public ApiResponse<CityDistributionResponse> getCityDistribution(){
        CityDistributionResponse response = memberStatisticService.getCityOrganization();
        return ApiResponse.ok(response);
    }

}
