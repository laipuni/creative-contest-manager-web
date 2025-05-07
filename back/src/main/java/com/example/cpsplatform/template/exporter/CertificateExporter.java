package com.example.cpsplatform.template.exporter;

import com.example.cpsplatform.certificate.domain.Certificate;

/**
 * 주의) open-session-in-view false 설정으로 @Transactional
 * 밖에서 사용시 LazyInitializationException 발생함
 */
public interface CertificateExporter {
    public byte[] export(Certificate certificate);

}
