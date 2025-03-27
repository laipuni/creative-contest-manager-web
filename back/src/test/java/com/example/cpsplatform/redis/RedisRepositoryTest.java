package com.example.cpsplatform.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisRepositoryTest {

    @Autowired
    RedisRepository redisRepository;

    @Autowired
    StringRedisTemplate redisTemplate;

    @DisplayName("해당 키로 저장된 값을 조회한다.")
    @Test
    void getData(){
        //given
        String expectedKey = "예상키";
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
        String expectedKey = "예상키";
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
        String expectedKey = "예상키";
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
        String expectedKey = "예상키";
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