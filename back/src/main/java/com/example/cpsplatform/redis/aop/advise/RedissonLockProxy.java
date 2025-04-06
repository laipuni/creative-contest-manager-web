package com.example.cpsplatform.redis.aop.advise;

import com.example.cpsplatform.redis.aop.annotaion.RedissonLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
public class RedissonLockProxy {

    private final RedissonClient redissonClient;

    @Around("@annotation(redissonLock)")
    public Object lock(final ProceedingJoinPoint joinPoint, RedissonLock redissonLock) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        EvaluationContext context = new StandardEvaluationContext();
        String prefix = redissonLock.prefix();
        String key = getDynamicValue(context, signature.getParameterNames(), joinPoint.getArgs(), redissonLock.key());
        String lockKey = prefix + ":" + key;// 예: "login:fail"

        RLock lock = redissonClient.getLock(lockKey);
        try{
            boolean available = lock.tryLock(
                    redissonLock.waitTime(),
                    redissonLock.leaseTime(),
                    redissonLock.timeUnit()
            );
            if (!available) {
                log.trace("Redisson Lock not available {} {}", method.getName(), key);
                throw new IllegalStateException("이미 처리 중인 요청입니다. 잠시 후 다시 시도해주세요.");
            }
            return joinPoint.proceed(); // 실제 메서드 실행
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock(); // 락 해제
            } else {
                log.trace("Redisson Lock Already UnLock {} {}", method.getName(), key);
            }
        }
    }

    private String getDynamicValue(EvaluationContext context, String[] paramNames, Object[] paramValues, String keyExpression) {
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], paramValues[i]);
            }
        }
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(keyExpression);
        return expression.getValue(context, String.class);
    }
}
