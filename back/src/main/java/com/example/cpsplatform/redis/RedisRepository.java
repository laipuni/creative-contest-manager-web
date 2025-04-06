package com.example.cpsplatform.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String,String> redisTemplate;

    public String handleException(Callable<String> callable){
        try {
            return callable.call();
        } catch (Exception e){
            log.error("Redis operation failed: {},", e.getMessage(),e.getCause());
            throw new IllegalStateException("서버에 문제가 생겨 해당서비스를 이용할 수 없습니다. 죄송합니다.",e);
        }
    }

    public String getData(final String key){
        return handleException(() ->{
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            return operations.get(key);
        });
    }

    public String setData(final String key, final String value){
        return handleException(() ->{
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            log.debug("set data for key: {}", key);
            operations.set(key,value);
            log.debug("Successfully set data for key: {}", key);
            return value;
        });
    }

    public String setDataWithTTL(final String key, final String value, final long ttl, final TimeUnit timeUnit){
        return handleException(() ->{
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            log.debug("set data with TTL for key: {} (TTL: {} {})", key, ttl, timeUnit);
            operations.set(key,value,ttl,timeUnit);
            log.debug("Successfully set data with TTL for key: {} (TTL: {} {})", key, ttl, timeUnit);
            return value;
        });
    }

    public String deleteData(final String key){
        return handleException(() ->{
            if(redisTemplate.hasKey(key)){
                log.debug("deleted data for key: {}", key);
                redisTemplate.delete(key);
                log.debug("Successfully deleted data for key: {}", key);
            }
            return null;
        });
    }
}
