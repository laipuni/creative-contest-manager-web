package com.example.cpsplatform.auth.service.session;

import com.example.cpsplatform.auth.service.InvalidSessionException;
import com.example.cpsplatform.auth.service.SessionService;
import com.example.cpsplatform.redis.RedisRepository;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RedisSessionService implements SessionService {

    public static final String PASSWORD_SESSION_KEY = "Password_Session_";

    private final RedisRepository redisRepository;

    public RedisSessionService(final RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    @Override
    public String storeSession(final String loginId, final SessionType sessionType) {
        String session = UUID.randomUUID().toString();
        String key = sessionType.getKey() + loginId;
        //해당 세션이 존재한다면 제거
        redisRepository.deleteData(key);
        //세션 저장
        redisRepository.setDataWithTTL(key, session, sessionType.getSessionTimeout(), TimeUnit.MINUTES);
        return session;
    }

    @Override
    public void confirmSession(final String loginId, final String session, final SessionType sessionType) {
        String key = sessionType.getKey() + loginId;
        String value = redisRepository.getData(key);
        if(!session.equals(value)){
            //해당 세션이 없거나 다를 경우
            throw new InvalidSessionException();
        }
    }
}
