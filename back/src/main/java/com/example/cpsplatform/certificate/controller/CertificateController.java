package com.example.cpsplatform.certificate.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.certificate.controller.response.UserSearchCertificateResponse;
import com.example.cpsplatform.certificate.domain.CertificateType;
import com.example.cpsplatform.certificate.repository.dto.UserSearchCertificateCond;
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
    public ApiResponse<UserSearchCertificateResponse> searchCertificates(@RequestParam(value = "page",defaultValue = "0") int page,
                                                                         @RequestParam(value = "page_size",defaultValue = "10") int pageSize,
                                                                         @RequestParam(value = "order",defaultValue = "desc") String order,
                                                                         @RequestParam(value = "type", defaultValue = "")String certificateType,
                                                                         @AuthenticationPrincipal SecurityMember securityMember){
        UserSearchCertificateResponse response = certificateService.searchCertificates(
                new UserSearchCertificateCond(
                        page,
                        pageSize,
                        order,
                        CertificateType.findCertificateType(certificateType),
                        securityMember.getUsername()
                )
        );
        return ApiResponse.ok(response);
    }

    @GetMapping("/api/certificates/{certificateId}")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable("certificateId") Long certificateId,
                                                      @AuthenticationPrincipal SecurityMember securityMember){
        DownloadCertificateResult result = certificateService.downloadCertificate(securityMember.getUsername(), certificateId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(String.format("%s.pdf",result.getCertificateName()))
                .build()
        );
        return new ResponseEntity<>(result.getFileBytes(), headers, HttpStatus.OK);
    }


}
