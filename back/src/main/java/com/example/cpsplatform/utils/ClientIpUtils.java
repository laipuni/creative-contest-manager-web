package com.example.cpsplatform.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * 클라이언트의 실제 IP 주소를 추출하는 유틸리티 클래스입니다.
 * 프록시(예: Nginx, AWS ELB 등)를 통해 전달된 요청의 경우,
 * HttpServletRequest의 getRemoteAddr()은 프록시 서버의 IP를 반환합니다.
 * 이 경우 "X-Forwarded-For" 헤더에 실제 클라이언트 IP가 포함되어 있으므로 해당 값을 우선적으로 사용
 */
public class ClientIpUtils {

    /**
     *  * X-Forwarded-For 헤더가 존재하면, 가장 앞에 있는 IP(실제 클라이언트 IP)를 반환
     *  * 없으면 request.getRemoteAddr() 값을 반환
     *  * IP 정보를 얻지 못하면 "unknown"을 반환
     */
    public static String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xff)) {
            return xff.split(",")[0].trim(); //첫 번째가 실제 클라이언트 IP
        }
        return Optional.ofNullable(request.getRemoteAddr()).orElse("unknown");
    }


}
