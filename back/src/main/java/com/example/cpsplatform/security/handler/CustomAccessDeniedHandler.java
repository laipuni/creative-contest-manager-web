package com.example.cpsplatform.security.handler;

import com.example.cpsplatform.utils.ClientIpUtils;
import com.example.cpsplatform.exception.controller.dto.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(final HttpServletRequest request, final HttpServletResponse response, final AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String clientIp = ClientIpUtils.getClientIp(request);
        String url = request.getRequestURI();
        log.warn("권한이 없는 유저(ip:{})가 url({})로 접근",clientIp,url);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        ApiErrorResponse<Object> errorResponse = ApiErrorResponse.of(
                HttpStatus.FORBIDDEN,
                "권한이 없습니다.",
                null
        );
        String body = new ObjectMapper().writeValueAsString(errorResponse);

        response.getWriter().write(body);
    }
}
