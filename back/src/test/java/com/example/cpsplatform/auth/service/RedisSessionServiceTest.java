package com.example.cpsplatform.auth.service;

import com.example.cpsplatform.auth.service.session.SessionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;

import static com.example.cpsplatform.auth.service.session.RedisSessionService.PASSWORD_SESSION_KEY;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RedisSessionServiceTest {

    //테스트용으로 id를 선언하고, 각각의 테스트에서 사용한 뒤 해당 id의 key값들만 제거하면 됨
    public static final String testId = "testId";

    @Autowired
    SessionService sessionService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void tearUp(){
        redisTemplate.delete(PASSWORD_SESSION_KEY + testId);
    }

    @DisplayName("아이디로 만든 키로 비밀번호 재설정 세션을 Redis에 저장한다.")
    @Test
    void storePasswordResetSession(){
        //given
        //when
        sessionService.storeSession(testId, SessionType.PASSWORD_RESET);
        //then
        assertThat(redisTemplate.hasKey(PASSWORD_SESSION_KEY + testId))
                .isTrue();
    }

    @DisplayName("비밀번호 재설정 세션을 확인한다.")
    @Test
    void confirmPasswordResetSession(){
        //given
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String session = UUID.randomUUID().toString();
        operations.set(PASSWORD_SESSION_KEY + testId, session);
        //when
        sessionService.confirmSession(testId,session,SessionType.PASSWORD_RESET);
        //then
        assertThat(redisTemplate.hasKey(PASSWORD_SESSION_KEY + testId))
                .isFalse();
    }

}