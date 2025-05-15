package com.example.cpsplatform.certificate.service;

import com.example.cpsplatform.certificate.controller.response.UserSearchCertificateResponse;
import com.example.cpsplatform.certificate.domain.Certificate;
import com.example.cpsplatform.certificate.domain.CertificateType;
import com.example.cpsplatform.certificate.repository.CertificateRepository;
import com.example.cpsplatform.certificate.repository.dto.UserSearchCertificateCond;
import com.example.cpsplatform.certificate.service.dto.DownloadCertificateResult;
import com.example.cpsplatform.exception.UnsupportedCertificateTypeException;
import com.example.cpsplatform.template.exporter.CertificateExporter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CertificateService {

    public static final int CERTIFICATE_PAGE_SIZE = 10;
    public static final String CERTIFICATE_SERVICE_LOG = "[CertificateService]";

    private final CertificateRepository certificateRepository;
    private final Map<CertificateType, CertificateExporter> certificateExporterMap;


    public UserSearchCertificateResponse searchCertificates(final UserSearchCertificateCond cond) {
        return certificateRepository.SearchUserCertificate(cond);
    }

    public DownloadCertificateResult downloadCertificate(final String loginId, final Long certificateId){
        //다운받을 유저가 본인의 확인증인지 검증한다.
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new IllegalArgumentException("해당 확인증은 존재하지 않습니다."));
        log.info("{} 사용자({})가 확인증(id:{})을 다운로드 시도합니다.",CERTIFICATE_SERVICE_LOG,loginId,certificateId);
        if(!certificate.isOwner(loginId)){
            //만약 자신의 확인증이 아닌 경우
            log.info("{} 사용자({})가 {}의 확인증(id:{})을 다운로드 시도했습니다.",
                    CERTIFICATE_SERVICE_LOG, loginId,certificate.getMember().getLoginId(),certificateId);
            throw new IllegalArgumentException("본인의 확인증만 다운로드할 수 있습니다.");
        }
        CertificateExporter exporter = findExporter(certificate.getCertificateType());
        return DownloadCertificateResult.of(exporter.export(certificate),certificate.getTitle());
    }

    private CertificateExporter findExporter(final CertificateType certificateType) {
        //확인증 유형에 맞게 변환할 exporter 찾기
        CertificateExporter certificateExporter = certificateExporterMap.get(certificateType);
        if(certificateExporter == null){
            //해당 유형의 확인증을 변환해줄 exporter가 존재하지 않는경우
            log.warn("{} 다운로드 지원하지 않는 {} 확인증 다운로드 시도",CERTIFICATE_SERVICE_LOG, certificateType.getLabel());
            throw new UnsupportedCertificateTypeException("해당 타입의 확인증은 지금 다운로드 할 수 없습니다. 죄송합니다.");
        }
        return certificateExporter;
    }
}
