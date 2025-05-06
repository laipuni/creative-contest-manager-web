package com.example.cpsplatform.template.config;

import com.example.cpsplatform.certificate.domain.CertificateType;
import com.example.cpsplatform.template.exporter.CertificateExporter;
import com.example.cpsplatform.template.exporter.PreliminaryCertificateExporter;
import com.example.cpsplatform.template.generator.CertificateGenerator;
import com.example.cpsplatform.template.generator.PdfCertificateGenerator;
import com.example.cpsplatform.template.renderer.TemplateRenderer;
import com.example.cpsplatform.template.renderer.ThymeleafTemplateRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TemplateConfig {

    @Autowired
    SpringTemplateEngine templateEngine;

    @Bean
    public CertificateGenerator certificateGenerator(){
        return new PdfCertificateGenerator();
    }

    @Bean
    public TemplateRenderer templateRenderer(){
        return new ThymeleafTemplateRenderer(templateEngine);
    }

    @Bean
    public Map<CertificateType, CertificateExporter> certificateExporterMap(){
        Map<CertificateType, CertificateExporter> certificateExporterMap = new HashMap<>();
        certificateExporterMap.put(
                CertificateType.PRELIMINARY, //예선 참가 확인증(접수증)
                new PreliminaryCertificateExporter(certificateGenerator(), templateRenderer())
        );
        return certificateExporterMap;
    }
}
