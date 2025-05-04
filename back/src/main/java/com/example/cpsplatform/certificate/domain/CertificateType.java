package com.example.cpsplatform.certificate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum CertificateType {

    PRELIMINARY("preliminary","예선"),
    FINAL("final","최종");

    final String key;
    final String label;
    final static Map<String,CertificateType> certificateTypeMap = Arrays.stream(CertificateType.values()).collect(
            Collectors.toMap(CertificateType::getKey,certificateType -> certificateType)
    );

    @JsonFormat
    public static CertificateType findCertificateType(String key){
        return certificateTypeMap.get(key.toLowerCase());
    }

}
