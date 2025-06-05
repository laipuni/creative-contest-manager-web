package com.example.cpsplatform.admin.aop;

import com.example.cpsplatform.utils.ClientIpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;


/**
 * 어드민 요청에 대한 로그를 기록하는 AOP 클래스
 * @AdminLog 애너테이션이 붙은 컨트롤러 메서드에 대해 사전 처리(@Before)로 작동
 * 관리자 요청의 URI, 요청자(username), IP 주소, 호출된 메서드명, 전달된 파라미터를 로그로 남김
 * 로그는 운영 중 관리자 페이지의 요청 추적 및 감사 목적으로 활용
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AdminLogProxy {

    private final HttpServletRequest request;

    @Before("@annotation(com.example.cpsplatform.admin.annotaion.AdminLog)")
    public void logAdminReqeust(JoinPoint joinPoint){
        String ip = ClientIpUtils.getClientIp(request);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String methodName = joinPoint.getSignature().toShortString();
        String uri = request.getRequestURI();
        Object[] args = joinPoint.getArgs();

        log.info("[ADMIN] url : {} by {} (IP: {}) | invoke method : {}, Payload: {}",
                uri, username, ip, methodName, Arrays.toString(args));
    }

}
