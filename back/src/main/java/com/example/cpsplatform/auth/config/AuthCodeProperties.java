package com.example.cpsplatform.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "auth.authcode")
public class AuthCodeProperties {
    long timeout = 5; // 기본값 설정 가능
    long passwordResetSessionTimeout = 10;
}
