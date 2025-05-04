package com.example.cpsplatform.certificate.admin.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.admin.annotaion.AdminLog;
import com.example.cpsplatform.certificate.admin.controller.request.DeleteCertificateRequest;
import com.example.cpsplatform.certificate.admin.service.CertificateAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @AdminLog
    @DeleteMapping("/api/admin/certificates")
    public ApiResponse<Object> deleteCertificate(@Valid @RequestBody DeleteCertificateRequest request){
        certificateAdminService.deleteCertificate(request.getCertificateId());
        return ApiResponse.ok(null);
    }

}
