package com.example.cpsplatform.certificate.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DownloadCertificateResult {

    private byte[] fileBytes;
    private String certificateName;

    public static DownloadCertificateResult of(final byte[] fileBytes, final String certificateName){
        return new DownloadCertificateResult(fileBytes,certificateName);
    }
}
