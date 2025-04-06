package com.example.cpsplatform.security.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.TestPropertySource;

import static com.example.cpsplatform.security.service.RedisLoginFailService.LOGIN_FAIL_COUNT_PREFIX;
import static com.example.cpsplatform.security.service.RedisLoginFailService.LOGIN_LOCK_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-auth.yml")
class RedisLoginFailServiceTest {

    @Value("${login.fail.count}")
    private int failCount;

    private static final String loginId = "username";

    @Autowired
    private RedisLoginFailService loginFailService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @BeforeEach
    public void cleanupRedis() {
        redisTemplate.delete(LOGIN_FAIL_COUNT_PREFIX + loginId);
        redisTemplate.delete(LOGIN_LOCK_PREFIX + loginId);
    }

    @DisplayName("해당 계정의 로그인을 처음 실패했을 경우, redis에 값과 실패횟수를 넣어준다.")
    @Test
    void failLoginWithFirstFailed(){
        //given
        //when
        loginFailService.failLogin(loginId,"testIp");
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        //then
        assertThat(redisTemplate.hasKey(LOGIN_FAIL_COUNT_PREFIX + loginId)).isTrue();
        assertThat(Long.parseLong(operations.get(LOGIN_FAIL_COUNT_PREFIX + loginId))).isEqualTo(1L);
    }

    @DisplayName("해당 계정의 로그인을 실패했을 경우, redis에 저장된 계정의 실패횟수를 올린다.")
    @Test
    void failLoginWithFailed(){
        //given
        String failCount = "1";
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(LOGIN_FAIL_COUNT_PREFIX + loginId, failCount);
        //when
        loginFailService.failLogin(loginId,"testIp");
        long count = Long.parseLong(operations.get(LOGIN_FAIL_COUNT_PREFIX + loginId));
        //then
        assertThat(redisTemplate.hasKey(LOGIN_FAIL_COUNT_PREFIX + loginId)).isTrue();
        assertThat(count).isEqualTo(2L);
    }


    @DisplayName("지정된 횟수이상 로그인 실패할 경우, 해당 계정에 로그인 잠금이 걸린다.")
    @Test
    void failLoginWithLock(){
        //given
        String failCountStr = String.valueOf(failCount - 1);
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(LOGIN_FAIL_COUNT_PREFIX + loginId, failCountStr);
        //when
        loginFailService.failLogin(loginId,"testIp");
        //then
        assertThat(redisTemplate.hasKey(LOGIN_FAIL_COUNT_PREFIX + loginId)).isFalse();
        assertThat(redisTemplate.hasKey(LOGIN_LOCK_PREFIX + loginId)).isTrue();
    }
}