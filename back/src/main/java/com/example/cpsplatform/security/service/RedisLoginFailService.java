package com.example.cpsplatform.security.service;

import com.example.cpsplatform.redis.RedisRepository;
import com.example.cpsplatform.redis.aop.annotaion.RedissonLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisLoginFailService implements LoginFailService{

    @Value("${login.fail.count}")
    private int failCount;

    @Value("${login.fail.time}")
    private Long LOGIN_FAIL_COUNT_TTL = 15L;

    @Value("${login.lock.time}")
    private Long LOGIN_LOCK_TTL = 15L;

    private final RedisRepository redisRepository;
    public static final String LOGIN_FAIL_COUNT_PREFIX = "Login_Fail_Count : ";
    public static final String LOGIN_LOCK_PREFIX = "Login_Lock : ";

    public RedisLoginFailService(final RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    @Override
    @RedissonLock(prefix = "login:fail", key = "#loginId")
    public void failLogin(final String loginId, final String clientIp) {
        if (isLockedAccount(loginId)){
            //이미 잠긴 유저일 경우
            log.info("[LOGIN LOCK] 잠긴 계정의 로그인 실패 시도 - loginId : {}, ip : {}", loginId,clientIp);
            return;
        }
        long count = incrementLoginFailCount(loginId);
        log.info("[LOGIN FAIL] {}번째 실패 - loginId : {}, ip : {}", count, loginId,clientIp);
        if (count >= failCount){
            //지정된 로그인 실패 횟수를 넘은 경우
            setLoginLock(loginId);
            log.warn("[LOGIN LOCK] 로그인 실패가 {}회 초과되어 계정을 잠급니다. loginId: {}, ip : {}", failCount, loginId,clientIp);
        }
    }

    private void setLoginLock(final String loginId){
        redisRepository.deleteData(LOGIN_FAIL_COUNT_PREFIX + loginId);
        redisRepository.setDataWithTTL(LOGIN_LOCK_PREFIX + loginId,loginId,LOGIN_LOCK_TTL, TimeUnit.MINUTES);
    }
    
    private long incrementLoginFailCount(final String loginId){
        String key = LOGIN_FAIL_COUNT_PREFIX + loginId;
        String value = redisRepository.getData(key);
        Long count = 1L;
        if(StringUtils.hasText(value)){
            count = Long.parseLong(value) + 1;
            redisRepository.deleteData(key);
        }
        redisRepository.setDataWithTTL(key, String.valueOf(count),LOGIN_FAIL_COUNT_TTL, TimeUnit.MINUTES);
        return count;
    }

    @Override
    public Boolean isLockedAccount(final String loginId) {
        return redisRepository.getData(LOGIN_LOCK_PREFIX + loginId) != null;
    }

    @Override
    public void successLogin(final String loginId, final String clientIp) {
        log.info("[LOGIN SUCCESS] loginId: {}, ip : {}", loginId, clientIp);
        redisRepository.deleteData(LOGIN_LOCK_PREFIX + loginId);
    }
}
