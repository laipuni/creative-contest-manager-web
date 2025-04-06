package com.example.cpsplatform.auth.sender;

import com.example.cpsplatform.auth.config.AuthCodeProperties;
import com.example.cpsplatform.auth.email.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

public class EmailAuthCodeSender implements AuthCodeSender{


    private final EmailService emailService;
    private final SpringTemplateEngine templateEngine;
    private final AuthCodeProperties authCodeProperties;

    public static final String AUTHCODE_MAIL_TITLE = "안녕하세요. 인증코드 메일입니다.";
    public static final String AUTHCODE_HTML_FORM_PATH = "/mail/authMailForm";

    public EmailAuthCodeSender(final EmailService emailService, final SpringTemplateEngine templateEngine, final AuthCodeProperties authCodeProperties) {
        this.emailService = emailService;
        this.templateEngine = templateEngine;
        this.authCodeProperties = authCodeProperties;
    }

    @Override
    public void sendAuthCode(final String recipient, final String authCode) {
        String content = resolveAuthCodeForm(getVariables(authCode));
        emailService.sendMail(recipient,AUTHCODE_MAIL_TITLE,content);
    }

    private Map<String, String> getVariables(final String authCode) {
        Map<String,String> variables = new HashMap<>();
        variables.put("authCode", authCode);
        variables.put("expireTime", String.valueOf(authCodeProperties.getTimeout()));
        return variables;
    }

    private String resolveAuthCodeForm(final Map<String,String> variables){
        Context context = new Context();
        variables.keySet()
                .forEach((value) ->
                        context.setVariable(value,variables.get(value))
                );
        return templateEngine.process(EmailAuthCodeSender.AUTHCODE_HTML_FORM_PATH, context);
    }
}
