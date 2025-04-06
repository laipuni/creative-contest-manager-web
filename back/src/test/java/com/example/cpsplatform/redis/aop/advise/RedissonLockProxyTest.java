package com.example.cpsplatform.redis.aop.advise;

import com.example.cpsplatform.redis.RedisRepository;
import com.example.cpsplatform.security.service.LoginFailService;
import com.example.cpsplatform.security.service.RedisLoginFailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.cpsplatform.security.service.RedisLoginFailService.LOGIN_FAIL_COUNT_PREFIX;
import static com.example.cpsplatform.security.service.RedisLoginFailService.LOGIN_LOCK_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedissonLockProxyTest {

    @Autowired
    private LoginFailService loginFailService;

    @Autowired
    private RedisRepository redisRepository;

    private final String loginId = "testId";

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @BeforeEach
    void init() {
        redisRepository.deleteData(LOGIN_FAIL_COUNT_PREFIX + loginId);
        redisRepository.deleteData(LOGIN_LOCK_PREFIX + loginId);
    }

    @Test
    @DisplayName("5개의 쓰레드가 동시에 로그인 실패를 시도해도 카운트는 정확히 5까지만 증가한다")
    void failLogin_withConcurrencyLimit() throws InterruptedException {
        //given
        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        //when
        for (int i = 0; i < threadCount; i++) {
            String clientIp = "testIp" + i;
            executorService.submit(() -> {
                try {
                    loginFailService.failLogin(loginId, clientIp);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await(); // 모든 쓰레드 완료 대기

        // then
        String lockKey = LOGIN_LOCK_PREFIX + loginId;
        assertThat(redisTemplate.hasKey(lockKey)).isTrue(); // 계정이 잠겨야 함
    }
}