package com.example.cpsplatform.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Repository;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String,Object> redisTemplate;

    public Object handleException(Callable<?> callable){
        try {
            return callable.call();
        } catch (Exception e){
            log.error("Redis operation failed: {}", e.getMessage(), e);
            throw new IllegalStateException("서버에 문제가 생겨 해당서비스를 이용할 수 없습니다. 죄송합니다.",e);
        }
    }

    public Object getData(final String key){
        return handleException(() ->{
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            return operations.get(key);
        });
    }

    public boolean setData(final String key, final Object value){
        return (boolean) handleException(() ->{
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            log.info("Successfully set data for key: {}", key);
            operations.set(key,value);
            return true;
        });
    }

    public boolean setDataWithTTL(final String key, final Object value, final long ttl, final TimeUnit timeUnit){
        return (boolean) handleException(() ->{
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            log.info("Successfully set data with TTL for key: {} (TTL: {} {})", key, ttl, timeUnit);
            operations.set(key,value,ttl,timeUnit);
            return true;
        });
    }

    public boolean deleteData(final String key){
        return (boolean) handleException(() ->{
            if(redisTemplate.hasKey(key)){
                redisTemplate.delete(key);
                log.info("Successfully deleted data for key: {}", key);
            }
            return true;
        });
    }
}
