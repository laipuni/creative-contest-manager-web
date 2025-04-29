package com.example.cpsplatform.admin.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AdminLogProxy {

    private final HttpServletRequest request;

    //@AdminLog가 있는 메서드들은 아래와 같은 로그를 남긴다.
    @Before("@annotation(com.example.cpsplatform.admin.annotaion.AdminLog)")
    public void logAdminReqeust(JoinPoint joinPoint){
        String ip = request.getRemoteUser();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String methodName = joinPoint.getSignature().toShortString();
        String uri = request.getRequestURI();
        Object[] args = joinPoint.getArgs();

        log.info("[ADMIN] url : {} by {} (IP: {}) | invoke method : {}, Payload: {}",
                uri, username, ip, methodName, Arrays.toString(args));
    }

}
