package com.example.cpsplatform.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static com.example.cpsplatform.security.service.RedisLoginFailService.LOGIN_FAIL_COUNT_PREFIX;
import static com.example.cpsplatform.security.service.RedisLoginFailService.LOGIN_LOCK_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RedisRepositoryTest {

    public static final String expectedKey = "예상키";

    @Autowired
    RedisRepository redisRepository;

    @Autowired
    StringRedisTemplate redisTemplate;

    @BeforeEach
    void tearUp(){
        redisTemplate.delete(LOGIN_FAIL_COUNT_PREFIX + expectedKey);
        redisTemplate.delete(LOGIN_LOCK_PREFIX + expectedKey);
    }

    @DisplayName("해당 키로 저장된 값을 조회한다.")
    @Test
    void getData(){
        //given
        String expectedValue = "예상값";

        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(expectedKey,expectedValue);
        //when
        String resultValue = redisRepository.getData(expectedKey);
        //then
        assertThat(resultValue).isEqualTo(expectedValue);
    }

    @DisplayName("키와 값을 저장한다")
    @Test
    void setData() {
        String expectedValue = "예상값";

        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(expectedKey,expectedValue);
        //when
        redisRepository.setData(expectedKey,expectedKey);

        //then
        Boolean result = redisTemplate.hasKey(expectedKey);
        assertThat(result).isTrue();
    }

    @DisplayName("ttl을 지정한 키와 값은 지정된 시간 후 삭제된다.")
    @Test
    void setDataExpire() throws InterruptedException {
        String expectedValue = "예상값";
        long duration = 1;

        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(expectedKey,expectedValue);
        //when
        redisRepository.setDataWithTTL(expectedKey,expectedKey,duration, TimeUnit.SECONDS);

        //then
        Thread.sleep(1100 * duration);
        Boolean result = redisTemplate.hasKey(expectedKey);
        assertThat(result).isFalse();
    }

    @DisplayName("key값을 받아 해당 키와 값를 삭제한다.")
    @Test
    void deleteData(){
        String expectedValue = "예상값";

        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(expectedKey,expectedValue);
        //when
        redisRepository.deleteData(expectedKey);

        //then
        Boolean result = redisTemplate.hasKey(expectedKey);
        assertThat(result).isFalse();
    }


}