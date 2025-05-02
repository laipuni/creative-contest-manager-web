package com.example.cpsplatform;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.util.Optional;

public class ClientIpUtils {

    public static String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xff)) {
            return xff.split(",")[0].trim(); //첫 번째가 실제 클라이언트 IP
        }
        return Optional.ofNullable(request.getRemoteAddr()).orElse("unknown");
    }


}
