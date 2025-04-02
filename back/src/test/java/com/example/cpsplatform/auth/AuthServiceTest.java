package com.example.cpsplatform.auth;

import com.example.cpsplatform.auth.storage.AuthCodeStorage;
import com.example.cpsplatform.auth.strategy.AuthCodeStrategy;
import com.example.cpsplatform.auth.strategy.RegisterAuthCodeStrategy;
import com.example.cpsplatform.exception.AuthCodeMismatchException;
import com.example.cpsplatform.exception.UnsupportedAuthenticationTypeException;
import com.example.cpsplatform.exception.UnsupportedSenderTypeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
class AuthServiceTest {

    @MockitoBean
    AuthCodeStorage authCodeStorage;

    @MockitoBean
    Map<String, AuthCodeStrategy> strategyMap;

    @Autowired
    AuthService authService;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @DisplayName("인증코드를 전송할 때, 해당 인증 전략이 존재하지 않는다면 예외 발생")
    @Test
    void sendAuthCodeUnsupportedAuthenticationType() {
        // given
        String recipient = "test@example.com";
        String senderType = "email";
        String strategyType = "invalidStrategy"; // 존재하지 않는 인증 전략

        // When & Then
        assertThatThrownBy(() -> authService.sendAuthCode(recipient, senderType, strategyType))
                .isInstanceOf(UnsupportedAuthenticationTypeException.class);
    }

    @DisplayName("인증코드를 전송할 때, 해당 전송 수단은 존재하지 않는다면 예외 발생")
    @Test
    void sendAuthCodeUnsupportedSenderType() {
        //given
        String recipient = "test@example.com";
        String senderType = "invalidSender"; // 존재하지 않는 전송 수단
        String strategyType = "register";

        RegisterAuthCodeStrategy authCodeStrategy = mock(RegisterAuthCodeStrategy.class);
        when(strategyMap.get(Mockito.anyString())).thenReturn(authCodeStrategy);
        when(authCodeStrategy.createKey(Mockito.anyString())).thenReturn("testKey");

        //WhenThen
        assertThatThrownBy(() -> authService.sendAuthCode(recipient, senderType, strategyType))
                .isInstanceOf(UnsupportedSenderTypeException.class);
    }


    @DisplayName("인증코드를 검증할 때, 해당 인증 전략이 존재하지 않는다면 예외 발생")
    @Test
    void verifyAuthCodeUnsupportedAuthenticationType() {
        // given
        String recipient = "test@example.com";
        String authCode = "123456";
        String strategyType = "invalidStrategy"; // 존재하지 않는 인증 전략

        // WhenThen
        assertThatThrownBy(() -> authService.verifyAuthCode(recipient, authCode, strategyType))
                .isInstanceOf(UnsupportedAuthenticationTypeException.class);
    }

    @DisplayName("인증코드를 검증할 때, 유효하지 않는 인증코즈일 경우 예외 발생")
    @Test
    void verifyAuthCodeWithInvalidAutoCode() {
        //given
        String recipient = "test@example.com";
        String authCode = "123456";
        String strategyType = "register";

        AuthCodeStrategy authCodeStrategy = mock(AuthCodeStrategy.class);
        when(strategyMap.get(strategyType)).thenReturn(authCodeStrategy);
        when(authCodeStrategy.createKey(recipient)).thenReturn("testKey");

        // Mocking 저장된 인증 코드
        when(authCodeStorage.findAuthCode("testKey")).thenReturn(authCode);

        // When & Then
        assertThatThrownBy(() -> authService.verifyAuthCode(recipient, "invalidCode", strategyType))
                .isInstanceOf(AuthCodeMismatchException.class);
    }

    @DisplayName("인증코드를 받아서 유효한 인증코드인지 레디스에서 검증한다.")
    @Test
    void verifyAuthCodeWithRedis() {
        // given
        String recipient = "test@example.com";
        String authCode = "123456";
        String strategyType = "register";
        String key = "REGISTER_" + recipient;

        //인증 코드 세팅(레디스)
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(key,authCode);

        //회원가입 인증 수단 Mocking
        AuthCodeStrategy authCodeStrategy = mock(RegisterAuthCodeStrategy.class);
        when(strategyMap.get(strategyType)).thenReturn(authCodeStrategy);
        when(authCodeStrategy.createKey(recipient)).thenReturn(key);

        //저장된 인증 코드 Mocking
        when(authCodeStorage.findAuthCode(Mockito.anyString())).thenReturn(authCode);

        // When
        // Then
        assertThat(authService.verifyAuthCode(recipient, authCode, strategyType)).isTrue();
    }
}