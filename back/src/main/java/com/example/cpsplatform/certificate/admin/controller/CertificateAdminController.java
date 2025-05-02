package com.example.cpsplatform.certificate.admin.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.admin.annotaion.AdminLog;
import com.example.cpsplatform.certificate.admin.service.CertificateAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CertificateAdminController {

    private final CertificateAdminService certificateAdminService;

    @AdminLog
    @PostMapping("/api/admin/contests/{contestId}/certificates/preliminary/batch")
    public ApiResponse<Object> batchPreliminaryCertificates(@PathVariable("contestId") Long contestId){
        certificateAdminService.batchCreatePreliminaryCertificates(contestId);
        return ApiResponse.ok(null);
    }

}
