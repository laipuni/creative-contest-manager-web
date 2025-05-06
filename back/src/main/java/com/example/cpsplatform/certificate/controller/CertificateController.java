package com.example.cpsplatform.certificate.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.certificate.controller.response.SearchCertificateResponse;
import com.example.cpsplatform.certificate.domain.CertificateType;
import com.example.cpsplatform.certificate.service.CertificateService;
import com.example.cpsplatform.certificate.service.dto.DownloadCertificateResult;
import com.example.cpsplatform.security.domain.SecurityMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping("/api/v1/certificates")
    public ApiResponse<SearchCertificateResponse> searchCertificates(@RequestParam(value = "page",defaultValue = "0") int page,
                                                                     @RequestParam(value = "order",defaultValue = "desc") String order,
                                                                     @RequestParam(value = "type", defaultValue = "")CertificateType certificateType,
                                                                     @AuthenticationPrincipal SecurityMember securityMember){
        SearchCertificateResponse response = certificateService.searchCertificates(page,order,certificateType,securityMember.getUsername());
        return ApiResponse.ok(response);
    }

}
