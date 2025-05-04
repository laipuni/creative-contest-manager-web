package com.example.cpsplatform.template.config;

import com.example.cpsplatform.template.generator.CertificateGenerator;
import com.example.cpsplatform.template.generator.PdfCertificateGenerator;
import com.example.cpsplatform.template.renderer.TemplateRenderer;
import com.example.cpsplatform.template.renderer.ThymeleafTemplateRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;

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


}
