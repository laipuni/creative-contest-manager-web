package com.example.cpsplatform.auth.storage;

import com.example.cpsplatform.auth.config.AuthCodeProperties;
import com.example.cpsplatform.auth.config.AuthConfig;
import com.example.cpsplatform.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.TimeUnit;

public class RedisAuthCodeStorage implements AuthCodeStorage {

    private final AuthCodeProperties authCodeProperties;

    private final RedisRepository redisRepository;

    public RedisAuthCodeStorage(final RedisRepository redisRepository, final AuthCodeProperties properties) {
        this.redisRepository = redisRepository;
        this.authCodeProperties = properties;
    }

    @Override
    public void storeAuthCode(final String key, final String authCode) {
        redisRepository.setDataWithTTL(key,authCode, authCodeProperties.getTimeout(), TimeUnit.MINUTES);
    }

    @Override
    public String findAuthCode(final String key) {
        return redisRepository.getData(key);
    }

    @Override
    public void removeAuthCode(final String key) {
        redisRepository.deleteData(key);
    }
}
