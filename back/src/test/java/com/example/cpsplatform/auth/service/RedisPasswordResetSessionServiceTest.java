package com.example.cpsplatform.auth.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisPasswordResetSessionServiceTest {

    //테스트용으로 id를 선언하고, 각각의 테스트에서 사용한 뒤 해당 id의 key값들만 제거하면 됨
    public static final String testId = "testId";

    @Autowired
    PasswordResetSessionService passwordResetSessionService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void tearUp(){
        redisTemplate.delete(RedisPasswordResetSessionService.PASSWORD_SESSION_KEY + testId);
    }

    @DisplayName("아이디로 만든 키로 비밀번호 재설정 세션을 Redis에 저장한다.")
    @Test
    void storePasswordResetSession(){
        //given
        //when
        passwordResetSessionService.storePasswordResetSession(testId);
        //then
        assertThat(redisTemplate.hasKey(RedisPasswordResetSessionService.PASSWORD_SESSION_KEY + testId))
                .isTrue();
    }

    @DisplayName("비밀번호 재설정 세션을 확인한다.")
    @Test
    void confirmPasswordResetSession(){
        //given
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String session = UUID.randomUUID().toString();
        operations.set(RedisPasswordResetSessionService.PASSWORD_SESSION_KEY + testId, session);
        //when
        passwordResetSessionService.confirmPasswordResetSession(testId,session);
        //then
        assertThat(redisTemplate.hasKey(RedisPasswordResetSessionService.PASSWORD_SESSION_KEY + testId))
                .isFalse();
    }

}