package com.example.cpsplatform.auth.service;

import com.example.cpsplatform.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RedisPasswordResetSessionService implements PasswordResetSessionService{

    @Value("${auth.password-reset-session.timeout}")
    private long sessionTimeout;

    public static final String PASSWORD_SESSION_KEY = "Password_Session_";

    private final RedisRepository redisRepository;

    public RedisPasswordResetSessionService(final RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    @Override
    public String storePasswordResetSession(final String loginId) {
        String session = UUID.randomUUID().toString();
        String key = PASSWORD_SESSION_KEY + loginId;
        redisRepository.setDataWithTTL(key, session, sessionTimeout, TimeUnit.MINUTES);
        return session;
    }

    @Override
    public void confirmPasswordResetSession(final String loginId, final String session) {
        String key = PASSWORD_SESSION_KEY + loginId;
        String value = redisRepository.getData(key);
        if(!session.equals(value)){
            //해당 비밀번호 재설정 세션이 없거나 다를 경우
            throw new InvalidPasswordResetSessionException();
        }
        redisRepository.deleteData(key);
    }
}
