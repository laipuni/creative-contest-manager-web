package com.example.cpsplatform.redis.aop.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLock {
    String key(); // SqEL로 표현된 값 (예: #email)
    String prefix(); // Lock key의 prefix
    long waitTime() default 5L; //락 대기시간 5초
    long leaseTime() default 3L; // 락 유지 시간 3초
    TimeUnit timeUnit() default TimeUnit.SECONDS; // 초 단위
}
