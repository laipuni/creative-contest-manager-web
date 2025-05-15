package com.example.cpsplatform.certificate.admin.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.admin.annotaion.AdminLog;
import com.example.cpsplatform.certificate.admin.controller.request.DeleteCertificateRequest;
import com.example.cpsplatform.certificate.admin.service.CertificateAdminService;
import com.example.cpsplatform.certificate.domain.CertificateType;
import com.example.cpsplatform.certificate.repository.dto.AdminSearchCertificateCond;
import com.example.cpsplatform.certificate.repository.dto.AdminSearchCertificateResponse;
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

    @AdminLog
    @GetMapping("/api/admin/certificates/search")
    public ApiResponse<AdminSearchCertificateResponse> searchCertificate(@RequestParam(value = "page",defaultValue = "0") int page,
                                                                    @RequestParam(value = "page_size",defaultValue = "10") int pageSize,
                                                                    @RequestParam(value = "order_type",defaultValue = "createdAt") String orderType,
                                                                    @RequestParam(value = "direction",defaultValue = "asc") String direction,
                                                                    @RequestParam(value = "certificate_type",defaultValue = "") String certificateType,
                                                                    @RequestParam(value = "search_type",defaultValue = "") String searchType,
                                                                    @RequestParam(value = "keyword",defaultValue = "") String keyword){
        AdminSearchCertificateResponse response = certificateAdminService.searchCertificates(new AdminSearchCertificateCond(
                page,
                pageSize,
                orderType,
                direction,
                CertificateType.findCertificateType(certificateType),
                searchType,
                keyword
        ));
        return ApiResponse.ok(response);
    }

}
