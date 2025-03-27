package com.example.cpsplatform.security.service;

import com.example.cpsplatform.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

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
    public void failLogin(final String loginId) {
        if (isLockedAccount(loginId)){
            //이미 잠긴 유저일 경우
            return;
        }
        long count = incrementLoginFailCount(loginId);
        if (count >= failCount){
            //지정된 로그인 실패 횟수를 넘은 경우
            setLoginLock(loginId);
        }
    }

    private void setLoginLock(final String loginId){
        redisRepository.deleteData(LOGIN_FAIL_COUNT_PREFIX + loginId);
        redisRepository.setDataWithTTL(LOGIN_LOCK_PREFIX + loginId,loginId,LOGIN_LOCK_TTL, TimeUnit.MINUTES);
    }
    
    private long incrementLoginFailCount(final String loginId){
        String key = LOGIN_FAIL_COUNT_PREFIX + loginId;
        Long count = Long.parseLong(redisRepository.getData(key));
        if(count == null){
            //로그인 실패 횟수가 저장이 안된 경우
            count = 1L;
        } else{
            count++;
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
    public void successLogin(final String loginId) {
        redisRepository.deleteData(LOGIN_LOCK_PREFIX + loginId);
    }
}
