package com.example.cpsplatform.security;

import com.example.cpsplatform.utils.ClientIpUtils;
import com.example.cpsplatform.exception.controller.dto.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@Slf4j
public class CustomEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomEntryPoint(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authException) throws IOException, ServletException {
        String clientIp = ClientIpUtils.getClientIp(request);
        String url = request.getRequestURI();
        log.info("인증되지 않은 유저(ip:{})가 url({})로 접근",clientIp,url);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        ApiErrorResponse<Object> errorResponse = ApiErrorResponse.of(
                HttpStatus.UNAUTHORIZED,
                "로그인이 필요합니다.",
                null
        );
        String body = new ObjectMapper().writeValueAsString(errorResponse);

        response.getWriter().write(body);
    }
}
