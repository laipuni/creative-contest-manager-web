package com.example.cpsplatform.auth.sender;

import com.example.cpsplatform.auth.config.AuthCodeProperties;
import com.example.cpsplatform.auth.email.EmailService;
import com.example.cpsplatform.template.renderer.TemplateRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

public class EmailAuthCodeSender implements AuthCodeSender{


    private final EmailService emailService;
    private final AuthCodeProperties authCodeProperties;
    private final TemplateRenderer templateRenderer;

    public static final String AUTHCODE_MAIL_TITLE = "안녕하세요. 인증코드 메일입니다.";
    // 앞에 "/" 붙일경우 절대 경로로 해석되어 templates아래의 html을 인식못할 수 있으니 주의
    public static final String AUTHCODE_HTML_FORM_PATH = "mail/authMailForm";

    public EmailAuthCodeSender(final EmailService emailService, final AuthCodeProperties authCodeProperties, final TemplateRenderer templateRenderer) {
        this.emailService = emailService;
        this.authCodeProperties = authCodeProperties;
        this.templateRenderer = templateRenderer;
    }

    @Override
    public void sendAuthCode(final String recipient, final String authCode) {
        String content = templateRenderer.render(getVariables(authCode),AUTHCODE_HTML_FORM_PATH);
        emailService.sendMail(recipient,AUTHCODE_MAIL_TITLE,content);
    }

    private Map<String, String> getVariables(final String authCode) {
        Map<String,String> variables = new HashMap<>();
        variables.put("authCode", authCode);
        variables.put("expireTime", String.valueOf(authCodeProperties.getTimeout()));
        return variables;
    }
}
