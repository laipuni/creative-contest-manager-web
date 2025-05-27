package com.example.cpsplatform.auth.config;

import com.example.cpsplatform.auth.service.AuthService;
import com.example.cpsplatform.auth.email.EmailService;
import com.example.cpsplatform.auth.generator.AuthCodeGenerator;
import com.example.cpsplatform.auth.generator.UUIDAuthCodeGenerator;
import com.example.cpsplatform.auth.sender.AuthCodeSender;
import com.example.cpsplatform.auth.sender.EmailAuthCodeSender;
import com.example.cpsplatform.auth.service.SessionService;
import com.example.cpsplatform.auth.service.session.RedisSessionService;
import com.example.cpsplatform.auth.storage.AuthCodeStorage;
import com.example.cpsplatform.auth.storage.RedisAuthCodeStorage;
import com.example.cpsplatform.auth.strategy.*;
import com.example.cpsplatform.redis.RedisRepository;
import com.example.cpsplatform.template.renderer.TemplateRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AuthConfig {

    public static final String REGISTER_AUTH = "register";
    public static final String FIND_ID_AUTH = "findId";
    public static final String PASSWORD_AUTH = "password_auth";
    public static final String SIGNUP_VERIFY_AUTH = "signup_verify";
    public static final String PROFILE_UPDATE_AUTH = "profile_update";
    public static final String PROFILE_UPDATE_VERIFY_AUTH = "profile_update_verify";


    @Autowired
    RedisRepository redisRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    AuthCodeProperties authCodeProperties;

    @Autowired
    TemplateRenderer templateRenderer;

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
    public SessionService passwordResetSessionService(){
        return new RedisSessionService(redisRepository);
    }

    @Bean
    public Map<String,AuthCodeStrategy> authCodeStrategy(){
        Map<String, AuthCodeStrategy> strategyMap = new HashMap<>();
        strategyMap.put(REGISTER_AUTH, new RegisterAuthCodeStrategy());
        strategyMap.put(FIND_ID_AUTH, new FindIdAuthCodeStrategy());
        strategyMap.put(PASSWORD_AUTH, new PasswordAuthCodeStrategy());
        strategyMap.put(SIGNUP_VERIFY_AUTH, new SignUpVerifyStrategy());
        strategyMap.put(PROFILE_UPDATE_AUTH, new ProfileUpdateAuthCodeStrategy());
        strategyMap.put(PROFILE_UPDATE_VERIFY_AUTH, new ProfileUpdateVerifyStrategy());
        return strategyMap;
    }

    @Bean
    public AuthCodeGenerator authCodeGenerator(){
        return new UUIDAuthCodeGenerator();
    }

    @Bean
    public Map<String,AuthCodeSender> authCodeSender(){
        Map<String, AuthCodeSender> authCodeSenderMap = new HashMap<>();
        authCodeSenderMap.put("email",new EmailAuthCodeSender(emailService, authCodeProperties,templateRenderer));
        return authCodeSenderMap;
    }

    @Bean
    public AuthCodeStorage authCodeStorage(){
        return new RedisAuthCodeStorage(redisRepository,authCodeProperties);
    }

}
