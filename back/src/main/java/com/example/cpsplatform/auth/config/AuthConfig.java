package com.example.cpsplatform.auth.config;

import com.example.cpsplatform.auth.service.AuthService;
import com.example.cpsplatform.auth.email.EmailService;
import com.example.cpsplatform.auth.generator.AuthCodeGenerator;
import com.example.cpsplatform.auth.generator.UUIDAuthCodeGenerator;
import com.example.cpsplatform.auth.sender.AuthCodeSender;
import com.example.cpsplatform.auth.sender.EmailAuthCodeSender;
import com.example.cpsplatform.auth.service.PasswordResetSessionService;
import com.example.cpsplatform.auth.service.RedisPasswordResetSessionService;
import com.example.cpsplatform.auth.storage.AuthCodeStorage;
import com.example.cpsplatform.auth.storage.RedisAuthCodeStorage;
import com.example.cpsplatform.auth.strategy.*;
import com.example.cpsplatform.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AuthConfig {

    public static final String REGISTER_AUTH = "register";

    @Autowired
    RedisRepository redisRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    SpringTemplateEngine springTemplateEngine;

    @Autowired
    AuthCodeProperties authCodeProperties;

    @Bean
    public AuthService authService(){
        return new AuthService(
                authCodeSender(),
                authCodeStorage(),
                authCodeStrategy(),
                authCodeGenerator()
        );
    }

    @Bean
    public PasswordResetSessionService passwordResetSessionService(){
        return new RedisPasswordResetSessionService(redisRepository);
    }

    @Bean
    public Map<String,AuthCodeStrategy> authCodeStrategy(){
        Map<String, AuthCodeStrategy> strategyMap = new HashMap<>();
        strategyMap.put("register",new RegisterAuthCodeStrategy());
        strategyMap.put("findId",new FindIdAuthCodeStrategy());
        strategyMap.put("password_auth",new PasswordAuthCodeStrategy());
        strategyMap.put("signup_verify",new SignUpVerifyStrategy());
        return strategyMap;
    }

    @Bean
    public AuthCodeGenerator authCodeGenerator(){
        return new UUIDAuthCodeGenerator();
    }

    @Bean
    public Map<String,AuthCodeSender> authCodeSender(){
        Map<String, AuthCodeSender> authCodeSenderMap = new HashMap<>();
        authCodeSenderMap.put("email",new EmailAuthCodeSender(emailService,springTemplateEngine, authCodeProperties));
        return authCodeSenderMap;
    }

    @Bean
    public AuthCodeStorage authCodeStorage(){
        return new RedisAuthCodeStorage(redisRepository,authCodeProperties);
    }

}
